package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;

/**
 * Pierwotny test nie weryfikował nic - asercje były niedokończone. Ponadto test był nieczytelny.
 */
class Done_ExceptionThrowingAgeVerificationTests {

	ExceptionThrowingAgeVerification verification = new ExceptionThrowingAgeVerification();
	
	@Test
	void should_verify_positively_when_person_is_an_adult() {
		assertThat(verification.passes(anAdult())).isTrue();
	}
	
	@Test
	void should_throw_exception_when_person_is_a_minor() {
		assertThatThrownBy(() -> verification.passes(aMinor())).hasMessageContaining("You cannot be below 18 years of age");
	}
	
	private Person anAdult() {
		return new Person("A", "B", LocalDate.now().minusYears(20), GENDER.FEMALE, "34567890");
	}
	
	private Person aMinor() {
		return new Person("A", "B", LocalDate.now(), GENDER.FEMALE, "34567890");
	}

}
