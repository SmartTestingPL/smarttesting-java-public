package pl.smarttesting.verifier.customer.verification;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

/**
 * Klasa zawiera przykłady asercji z wykorzystaniem bibliotek do asercji.
 */
class AgeVerificationTest {

	@Test
	void verificationShouldPassForAgeBetween18And99() {
		// given
		Person person = buildPerson(22);
		AgeVerification verification = new AgeVerification(new EventEmitter());

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isTrue();
	}

	@Test
	void shouldReturnFalseWhenUserOlderThan99() {
		// given
		Person person = buildPerson(100);
		AgeVerification verification = new AgeVerification(new EventEmitter());

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isFalse();
	}

	@Test
	void testIllegalStateExceptionThrownWhenAgeBelowZero() {
		// given
		Person person = buildPerson(-1);
		AgeVerification verification = new AgeVerification(new EventEmitter());

		assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> verification.passes(person));
	}

	private Person buildPerson(int age) {
		LocalDate birthDate = LocalDate.now().minusYears(age);
		return new Person("Anna", "Smith", birthDate,
				Person.GENDER.FEMALE, "00000000000");
	}
}