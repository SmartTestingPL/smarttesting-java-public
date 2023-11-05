package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;

/**
 * Test weryfikujący efekt uboczny w postaci wywołania metody asynchronicznie.
 */
@SpringBootTest(classes = _06_AsyncCustomerWithSpyVerifierTests.Config.class)
class _06_AsyncCustomerWithSpyVerifierTests {

	@Autowired _01_CustomerVerifier verifier;
	@Autowired FraudAlertNotifier verificationNotifier;

	/**
	 * Nie możemy zrobić `BDDMockito.then(fraudAlertNotifier).should().fraudFound(any())` w asercji
	 * tak jak poniżej:
	 *
	 * await().untilAsserted(() ->
	 * 	BDDMockito.then(fraudAlertNotifier).should().fraudFound(any()));
	 *
	 * ponieważ wywołanie `BDDMockito.then(fraudAlertNotifier).should().fraudFound(any())` faktycznie wywołałoby metodę na szpiegu. Wówczas w logach mielibyśmy dwa wywołania logowania.
	 */
	@Test
	void should_delegate_work_to_a_separate_thread() {
		verifier.foundFraud(new Customer(UUID.randomUUID(), tooYoungStefan()));

		await().untilAsserted(() ->
				BDDMockito.willDoNothing().given(verificationNotifier).fraudFound(any()));
	}

	Person tooYoungStefan() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "0123456789");
	}

	/**
	 * Tworzymy konfigurację, w której nie chcemy mieć prawdziwego komponentu
	 * {@link FraudAlertNotifier}. Tworzymy ręcznie implementację, z adnotowaną metodą
	 * adnotacją {@link Async} i tworzymy Spy, żeby móc dokonać weryfikacji w teście.
	 * Ponadto włączamy obsługę procesowania asynchronicznego poprzez adnotację {@link EnableAsync}.
	 */
	@Configuration(proxyBeanMethods = false)
	@EnableAsync
	@EnableAutoConfiguration
	static class Config extends CustomerConfiguration {
		private static final Logger log = LoggerFactory.getLogger(Config.class);

		@Override
		FraudAlertNotifier fraudAlertNotifier() {
			// Szpieg, żeby zrobić adnotację i implementacja, żeby delegować do osobnego wątku
			return BDDMockito.spy(new _04_VerificationNotifier() {
				// Ważne! Adnotujemy @Async
				@Async
				@Override
				public void fraudFound(CustomerVerification customerVerification) {
					log.info("Hello");
				}
			});
		}
	}
}
