package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import reactor.test.StepVerifier;

import static org.assertj.core.api.BDDAssertions.then;
import static pl.smarttesting.verifier.model.verification.VerificationAccessor.verifications;

/**
 * Testy do kodu reaktywnego z użyciem natywnych narzędzi testowych biblioteki Reactor.
 */
public class _08_ReactorCustomerVerifierTests {

	/**
	 * Testujemy kod jednowątkowy.
	 */
	@Test
	void should_work_with_reactor() {
		_01_CustomerVerifier verifier = new _01_CustomerVerifier(verifications());

		StepVerifier.create(verifier.verifyFlux(new Customer(UUID.randomUUID(), tooYoungStefan()))
				.map(r -> r.verificationName)
				.collectList())
				.assertNext(strings -> then(strings).containsOnly("id", "name", "age"))
				.expectComplete()
				.verify();
	}

	/**
	 * Testujemy kod wielowątkowy.
	 */
	@Test
	void should_work_with_parallel_reactor() {
		_01_CustomerVerifier verifier = new _01_CustomerVerifier(verifications());

		StepVerifier.create(verifier.verifyParallelFlux(new Customer(UUID.randomUUID(), tooYoungStefan()))
				.map(r -> r.verificationName)
				.sequential(3)
				.collectList())
				.assertNext(strings -> then(strings).containsOnly("id", "name", "age"))
				.expectComplete()
				.verify();
	}

	Person tooYoungStefan() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "0123456789");
	}
}
