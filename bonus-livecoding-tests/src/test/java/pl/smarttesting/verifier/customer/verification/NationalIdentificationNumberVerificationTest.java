package pl.smarttesting.verifier.customer.verification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Klasa zawiera przykłady testów parametryzowanych.
 */
class NationalIdentificationNumberVerificationTest {

	static Stream<Arguments> idVerificationArgumentsProvider() {
		return Stream.of(
				arguments(LocalDate.of(1998, 3, 14), Person.GENDER.FEMALE, true),
				arguments(LocalDate.of(1998, 3, 14), Person.GENDER.MALE, false),
				arguments(LocalDate.of(2000, 3, 14), Person.GENDER.FEMALE, false)
		);
	}

	@Test
	void verificationShouldPassForCorrectIdentificationNumber() {
		//given
		Person person = buildPerson(LocalDate.of(1998, 3, 14), Person.GENDER.FEMALE);
		IdentificationNumberVerification verification = new IdentificationNumberVerification(new EventEmitter());

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isTrue();
	}

	@Test
	void verificationShouldFailForInconsistentGender() {
		//given
		Person person = buildPerson(LocalDate.of(1998, 3, 14), Person.GENDER.MALE);
		IdentificationNumberVerification verification = new IdentificationNumberVerification(new EventEmitter());

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isFalse();
	}

	@Test
	void shouldReturnFalseForWrongYearOfBirth() {
		//given
		Person person = buildPerson(LocalDate.of(2000, 3, 14), Person.GENDER.FEMALE);
		IdentificationNumberVerification verification = new IdentificationNumberVerification(new EventEmitter());

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isFalse();
	}

	// Test tych samych przypadków co w 3 różnych testach powyżej przy pomocy
	// parametrów zwracanych przez metodę.
	@ParameterizedTest(name = "should return {2} for birth date {0} and gender {1}")
	@MethodSource("idVerificationArgumentsProvider")
	void shouldVerifyNationalIdentificationNumberAgainstPersonalData(LocalDate birthDate,
			Person.GENDER gender, boolean passes) {
		//given
		Person person = buildPerson(birthDate, gender);
		IdentificationNumberVerification verification = new IdentificationNumberVerification(
				new EventEmitter());

		// when
		boolean actualPasses = verification.passes(person);

		// then
		assertThat(actualPasses).isEqualTo(passes);

	}

	// Test tych samych przypadków co w 3 różnych testach powyżej przy pomocy
	// parametrów z pliku CSV.
	@ParameterizedTest(name = "should return {2} for birth date {0} and gender {1}")
	@CsvFileSource(resources = "/pesel.csv", numLinesToSkip = 1)
	void shouldVerifyNationalIdentificationNumberAgainstPersonalDataFromFile(LocalDate birthDate,
			Person.GENDER gender, boolean passes) {
		//given
		Person person = buildPerson(birthDate, gender);
		IdentificationNumberVerification verification = new IdentificationNumberVerification(
				new EventEmitter());

		// when
		boolean actualPasses = verification.passes(person);

		// then
		assertThat(actualPasses).isEqualTo(passes);

	}

	private Person buildPerson(LocalDate birthDate, Person.GENDER gender) {
		return new Person("John", "Doe", birthDate, gender, "98031416402");
	}
}