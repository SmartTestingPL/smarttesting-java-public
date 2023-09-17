package pl.smarttesting.verifier.customer.verification;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

/**
 * Klasa zawiera przykłady wykorzystania bibliotek do asercji. Tu akurat używamy biblioteki
 * AssertJ, którą polecamy dla Javy.
 */
class AgeVerificationTest {

	@Test
	void verificationShouldPassForAgeBetween18And99() {
		// given
		Person person = buildPerson(22);
		AgeVerification verification = new AgeVerification();

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isTrue();
	}

	@Test
	void shouldReturnFalseWhenUserOlderThan99() {
		// given
		Person person = buildPerson(100);
		AgeVerification verification = new AgeVerification();

		// when
		boolean passes = verification.passes(person);

		// then
		assertThat(passes).isFalse();
	}

	// Weryfikacja wyjątku przy pomocy biblioteki do asercji.
	@Test
	void testIllegalStateExceptionThrownWhenAgeBelowZero() {
		// given
		Person person = buildPerson(-1);
		AgeVerification verification = new AgeVerification();

		assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> verification.passes(person));
	}

	// Metoda pomocnicza tworząca obiekty wykorzystywane w testach używana w celu uzyskania
	// lepszej czytelności kodu i reużycia kodu.
	private Person buildPerson(int age) {
		LocalDate birthDate = LocalDate.now().minusYears(age);
		return new Person("Anna", "Smith", birthDate,
				Person.GENDER.FEMALE, "00000000000");
	}
}