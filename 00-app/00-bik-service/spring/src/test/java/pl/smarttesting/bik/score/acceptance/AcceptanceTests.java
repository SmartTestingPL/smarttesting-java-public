package pl.smarttesting.bik.score.acceptance;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.bik.score.BikApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Dotyczy lekcji 04-01
@SpringBootTest(classes = BikApplication.class, properties = {
		"bik.score.threshold=1300",
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-db-cache",
		"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect"})
@Testcontainers
@AutoConfigureMockMvc
class AcceptanceTests implements Infrastructure {

	static {
		rabbit.start();
		mongoDBContainer.start();
		Infrastructure.startHttpServers();
	}

	@DynamicPropertySource
	static void rabbitProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
		registry.add("social.url", socialService::baseUrl);
		registry.add("personal.url", personalService::baseUrl);
		registry.add("monthly-cost.url", monthlyCostService::baseUrl);
		registry.add("monthly-income.url", incomeService::baseUrl);
	}

	@Test
	void should_return_failed_verification_when_fraud_applies_for_a_check(@Autowired MockMvc mockMvc) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(pesel(withFraudPesel())))
				.andExpect(status().isForbidden())
				.andExpect(content().string(containsString("status\":\"VERIFICATION_FAILED\"")));
	}

	@Test
	void should_return_passed_verification_when_non_fraud_applies_for_a_check(@Autowired MockMvc mockMvc) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(pesel(withNonFraudPesel())))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("status\":\"VERIFICATION_PASSED\"")));
	}

	private String withNonFraudPesel() {
		return "89050193724";
	}

	private String withFraudPesel() {
		return "12345678901";
	}

	private URI pesel(String pesel) {
		return URI.create("/" + pesel);
	}
}
