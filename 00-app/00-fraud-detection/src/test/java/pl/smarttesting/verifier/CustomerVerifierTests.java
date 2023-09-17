package pl.smarttesting.verifier;

import java.time.LocalDate;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.BDDAssertions.then;
import static pl.smarttesting.verifier.CustomerVerificationResult.Status.VERIFICATION_PASSED;

// Dotyczy lekcji 03-05
@DataJpaTest(properties = {
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-customer-verifier",
		"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect"})
@ContextConfiguration(classes = CustomerVerifierTests.Config.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerVerifierTests {

	@Test
	void should_successfully_verify_a_customer_when_previously_verified(@Autowired VerificationRepository repository, @Autowired CustomerVerifier verifier) {
		Customer zbigniew = zbigniew();
		then(repository.findByUserId(zbigniew.getUuid())).isPresent();

		CustomerVerificationResult result = verifier.verify(zbigniew);

		then(result.getUserId()).isEqualTo(zbigniew().getUuid());
		then(result.getStatus()).isEqualTo(VERIFICATION_PASSED);
	}

	private Customer zbigniew() {
		return new Customer(UUID.fromString("89c878e3-38f7-4831-af6c-c3b4a0669022"), youngZbigniew());
	}

	private Person youngZbigniew() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "18210116954");
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class Config {

		@Bean
		MeterRegistry meterRegistry() {
			return new SimpleMeterRegistry();
		}

		@Bean
		FraudAlertNotifier fraudAlertNotifier() {
			return BDDMockito.mock(FraudAlertNotifier.class);
		}

		@Bean
		CustomerVerifier testCustomerVerifier(BIKVerificationService bikVerificationService, VerificationRepository verificationRepository, FraudAlertNotifier fraudAlertNotifier, MeterRegistry meterRegistry) {
			return new CustomerVerifier(bikVerificationService, new SimpleObjectProvider<>(null), verificationRepository, fraudAlertNotifier, meterRegistry);
		}

		@Bean
		BIKVerificationService exceptionThrowingBikVerificationService() {
			return new BIKVerificationService("", null) {
				@Override
				public CustomerVerificationResult verify(Customer customer) {
					throw new IllegalStateException("BOOM!");
				}
			};
		}
	}
}
