package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.assertj.core.api.BDDAssertions.then;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(classes = ExceptionCustomerVerifierTests.Config.class)
class ExceptionCustomerVerifierTests {

	/**
	 * Zakładamy, z punktu widzenia, biznesowego, że potrafimy obsłużyć sytuację rzucenia wyjątku.
	 * W naszym przypadku jest to uzyskanie wyniku procesowania klienta nawet jeśli wyjątek został rzucony.
	 * Nie chcemy sytuacji, w której rzucony błąd wpłynie na nasz proces biznesowy.
	 *
	 * Zakomentuj adnotację {@link Disabled}, żeby przekonać się, że test może nie przejść!
	 *
	 * @param verifier - komponent do przetestowania
	 */
	@Disabled
	@Test
	void should_handle_exceptions_gracefully_when_dealing_with_results(@Autowired
			_01_CustomerVerifier verifier) {
		List<VerificationResult> results = verifier.verify(new Customer(UUID.randomUUID(), tooYoungStefan()));

		await().untilAsserted(() -> then(results)
				.containsOnly(new VerificationResult("exception", false)));
	}

	/**
	 * Poprawiamy problem z kodu wyżej. Metoda produkcyjna {@link _01_CustomerVerifier#verifyNoException(Customer)} potrafi obsłużyć rzucony wyjątek z osobnego wątku.
	 *
	 * @param verifier - komponent do przetestowania
	 */
	@Test
	void should_handle_exceptions_gracefully_when_dealing_with_results_passing(@Autowired
			_01_CustomerVerifier verifier) {
		List<VerificationResult> results = verifier.verifyNoException(new Customer(UUID.randomUUID(), tooYoungStefan()));

		await().untilAsserted(() -> then(results)
				.containsOnly(new VerificationResult("exception", false)));
	}

	/**
	 * W przypadku Springa, domyślnie, metoda adnotowana {@link Async} jeśli rzuci wyjątek, to zostanie
	 * on po prostu zalogowany.
	 *
	 * W tym teście chcemy się upewnić, że takie procesowanie nie wpływa na nasz proces biznesowy. W tym celu
	 * nadpisujemy główną implementację zamieniając ją na {@link ExceptionThrowingFraudNotifier}, który możemy
	 * zweryfikować czy miał swoją metodę wykonaną, zanim wyjątek został rzucony.
	 *
	 * @param verifier - komponent, który testujemy
	 * @param verificationNotifier - sztuczna implementacja rzucająca wyjątek w wyniku procesowania asynchronicznego
	 */
	@Test
	void should_handle_exceptions_gracefully(@Autowired
			_01_CustomerVerifier verifier, @Autowired ExceptionThrowingFraudNotifier verificationNotifier) {
		verifier.foundFraud(new Customer(UUID.randomUUID(), tooYoungStefan()));

		await().untilAtomic(verificationNotifier.isExecuted(), equalTo(true));
	}

	Person tooYoungStefan() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "0123456789");
	}

	@Configuration(proxyBeanMethods = false)
	@EnableAsync(proxyTargetClass = true)
	@EnableAutoConfiguration
	static class Config extends CustomerConfiguration {

		@Bean
		Verification exceptionThrowingVerification() {
			return new _07_ExceptionThrowingVerification();
		}

		@Override
		FraudAlertNotifier fraudAlertNotifier() {
			return new ExceptionThrowingFraudNotifier();
		}
	}
}

/**
 * Testowa implementacja weryfikacji, która będzie rzucać wyjątkiem.
 */
class _07_ExceptionThrowingVerification implements Verification {

	private static final Logger log = LoggerFactory.getLogger(_07_ExceptionThrowingVerification.class);

	@Override
	public VerificationResult passes(Person person) {
		log.info("Running this in a separate thread");
		throw new IllegalStateException("Boom!");
	}

	@Override
	public String name() {
		return "exception";
	}
}

class ExceptionThrowingFraudNotifier implements FraudAlertNotifier {
	private static final Logger log = LoggerFactory.getLogger(ExceptionThrowingFraudNotifier.class);

	final AtomicBoolean executed = new AtomicBoolean();

	/**
	 * Jeśli metoda została wykonana, pole {@link this#executed} zostanie ustawione na {@code true}.
	 * Następnie rzuca wyjątek.
	 * @param customerVerification - weryfikacja klienta
	 */
	@Async
	@Override
	public void fraudFound(CustomerVerification customerVerification) {
		executed.set(true);
		log.info("Running fraud notification in a new thread");
		throw new IllegalStateException("Boom!");
	}

	AtomicBoolean isExecuted() {
		return this.executed;
	}
}