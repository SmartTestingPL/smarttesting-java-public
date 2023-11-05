package pl.smarttesting.verifier.model.verification;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.VerificationResult;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

class _02_AgeVerificationTests {

	@Test
	void should_throw_exception_when_age_invalid() {
		_01_AgeVerification verification = new _01_AgeVerification();

		thenThrownBy(() -> verification.passes(zbigniewFromTheFuture()))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Age cannot be negative");
	}

	Person zbigniewFromTheFuture() {
		return new Person("Zbigniew", "Stefanowski", LocalDate.now().plusYears(10), Person.GENDER.MALE, "1234567890");
	}

	@Test
	void should_return_positive_verification_when_age_is_within_the_threshold() {
		_01_AgeVerification verification = new _01_AgeVerification();

		VerificationResult result = verification.passes(oldEnoughZbigniew());

		then(result.result).isTrue();
	}

	Person oldEnoughZbigniew() {
		return new Person("Zbigniew", "Stefanowski", LocalDate.now().minusYears(25), Person.GENDER.MALE, "1234567890");
	}

	@Test
	void should_return_negative_verification_when_age_is_below_the_threshold() {
		_01_AgeVerification verification = new _01_AgeVerification();

		VerificationResult result = verification.passes(tooYoungZbigniew());

		then(result.result).isFalse();
	}

	Person tooYoungZbigniew() {
		return new Person("Zbigniew", "Stefanowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}

	@Test
	void should_return_negative_verification_when_age_is_above_the_threshold() {
		_01_AgeVerification verification = new _01_AgeVerification();

		VerificationResult result = verification.passes(tooOldZbigniew());

		then(result.result).isFalse();
	}

	Person tooOldZbigniew() {
		return new Person("Zbigniew", "Stefanowski", LocalDate.now().minusYears(1000), Person.GENDER.MALE, "1234567890");
	}

	/**
	 * Odkomentuj {@link Disabled}, żeby zwiększyć pokrycie kodu testami. Pokrywamy warunki brzegowe!
	 */
	@Disabled
	@Test
	void should_return_negative_verification_when_age_is_in_lower_boundary() {
		_01_AgeVerification verification = new _01_AgeVerification();

		VerificationResult result = verification.passes(lowerAgeBoundaryZbigniew());

		then(result.result).isTrue();
	}

	Person lowerAgeBoundaryZbigniew() {
		return new Person("Zbigniew", "Stefanowski", LocalDate.now().minusYears(18), Person.GENDER.MALE, "1234567890");
	}

	/**
	 * Odkomentuj {@link Disabled}, żeby zwiększyć pokrycie kodu testami. Pokrywamy warunki brzegowe!
	 */
	@Disabled
	@Test
	void should_return_negative_verification_when_age_is_in_upper_boundary() {
		_01_AgeVerification verification = new _01_AgeVerification();

		VerificationResult result = verification.passes(upperAgeBoundaryZbigniew());

		then(result.result).isTrue();
	}

	Person upperAgeBoundaryZbigniew() {
		return new Person("Zbigniew", "Stefanowski", LocalDate.now().minusYears(99), Person.GENDER.MALE, "1234567890");
	}
}