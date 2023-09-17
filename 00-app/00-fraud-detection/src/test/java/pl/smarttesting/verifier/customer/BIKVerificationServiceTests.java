package pl.smarttesting.verifier.customer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.BIKVerificationService;
import pl.smarttesting.verifier.CustomerVerificationResult;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerPort;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.BDDAssertions.then;

@ExtendWith(SpringExtension.class)
@AutoConfigureStubRunner(ids = "pl.smarttesting:00-bik-service-spring", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class BIKVerificationServiceTests {

	@StubRunnerPort("00-bik-service-spring")
	int port;

	BIKVerificationService bikVerificationService;

	@BeforeEach
	void setup() {
		bikVerificationService = new BIKVerificationService("http://localhost:" + port, new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofMillis(1000))
				.build());
	}

	@Test
	void should_return_passed_for_non_fraud() {
		UUID nonFraudUuid = UUID.fromString("5cd495e7-9a66-4c4b-bba2-8d15cc8d9e68");
		String nonFraudNationalIdNumber = "89050193724";

		CustomerVerificationResult result = bikVerificationService.verify(new Customer(nonFraudUuid, new Person("a", "b", LocalDate.now(), Person.GENDER.MALE, nonFraudNationalIdNumber)));

		then(result.getUserId()).isEqualTo(nonFraudUuid);
		then(result.passed()).isTrue();
	}

	@Test
	void should_return_failed_for_fraud() {
		UUID fraudUuid = UUID.fromString("cc8aa8ff-40ff-426f-bc71-5bb7ea644108");
		String fraudNationalIdNumber = "00262161334";

		CustomerVerificationResult result = bikVerificationService.verify(new Customer(fraudUuid, new Person("a", "b", LocalDate.now(), Person.GENDER.MALE, fraudNationalIdNumber)));

		then(result.getUserId()).isEqualTo(fraudUuid);
		then(result.passed()).isFalse();
	}
}