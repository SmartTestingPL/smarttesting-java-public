package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.mockito.ArgumentMatchers.any;

/**
 * Test weryfikujący efekt uboczny w postaci wywołania metody asynchronicznie.
 */
@SpringBootTest(classes = _05_AsyncCustomerVerifierTests.Config.class)
class _05_AsyncCustomerVerifierTests {

	@Autowired _01_CustomerVerifier verifier;
	@Autowired _04_VerificationNotifier verificationNotifier;

	@Test
	void should_notify_about_fraud() {
		verifier.foundFraud(new Customer(UUID.randomUUID(), tooYoungStefan()));

		BDDMockito.then(verificationNotifier).should().fraudFound(any());
	}

	Person tooYoungStefan() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "0123456789");
	}

	/**
	 * Tworzymy konfigurację, w której nie chcemy mieć prawdziwego komponentu
	 * {@link FraudAlertNotifier}.
	 */
	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	static class Config extends CustomerConfiguration {
		@Override
		_04_VerificationNotifier fraudAlertNotifier() {
			return BDDMockito.mock(_04_VerificationNotifier.class);
		}
	}
}
