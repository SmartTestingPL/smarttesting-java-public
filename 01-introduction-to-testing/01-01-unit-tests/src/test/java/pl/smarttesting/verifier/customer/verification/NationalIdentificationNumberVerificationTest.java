package pl.smarttesting.verifier.customer.verification;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// Klasa zawiera przykłady różnych konwencji nazewniczych metod testowych. Warto jest trzymać
// się jednej dla całej organizacji.
class NationalIdentificationNumberVerificationTest {

	@Test
	void verificationShouldPassForCorrectIdentificationNumber() {
		//given
		Person person = buildPerson(LocalDate.of(1998, 3, 14), Person.GENDER.FEMALE);
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isTrue();
	}

	@Test
	void verificationShouldFailForInconsistentGender() {
		//given
		Person person = buildPerson(LocalDate.of(1998, 3, 14), Person.GENDER.MALE);
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isFalse();
	}

	@Test
	void shouldReturnFalseForWrongYearOfBirth() {
		//given
		Person person = buildPerson(LocalDate.of(2000, 3, 14), Person.GENDER.FEMALE);
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isFalse();
	}

	private Person buildPerson(LocalDate birthDate, Person.GENDER gender) {
		return new Person("John", "Doe", birthDate, gender, "98031416402");
	}
}