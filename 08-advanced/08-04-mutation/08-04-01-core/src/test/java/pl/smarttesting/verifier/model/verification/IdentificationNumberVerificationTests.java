package pl.smarttesting.verifier.model.verification;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.VerificationResult;

import static org.assertj.core.api.BDDAssertions.then;

class IdentificationNumberVerificationTests {

	@Test
	void should_return_positive_verification_when_gender_female_corresponds_to_id_number() {
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		VerificationResult result = verification.passes(annaTheWoman());

		then(result.result).isTrue();
	}

	@Test
	void should_return_negative_verification_when_gender_female_does_not_correspond_to_id_number() {
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		VerificationResult result = verification.passes(annaWithNonFemaleId());

		then(result.result).isFalse();
	}

	@Test
	void should_return_positive_verification_when_gender_male_corresponds_to_id_number() {
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		VerificationResult result = verification.passes(zbigniewTheMan());

		then(result.result).isTrue();
	}

	@Test
	void should_return_negative_verification_when_gender_male_does_not_correspond_to_id_number() {
		IdentificationNumberVerification verification = new IdentificationNumberVerification();

		VerificationResult result = verification.passes(zbigniewWithNonMaleId());

		then(result.result).isFalse();
	}

	Person annaTheWoman() {
		return new Person("Anna", "Annowska", LocalDate.now(), Person.GENDER.FEMALE, "00000000020");
	}

	Person annaWithNonFemaleId() {
		return new Person("Anna", "Annowska", LocalDate.now(), Person.GENDER.FEMALE, "00000000010");
	}

	Person zbigniewTheMan() {
		return new Person("Zbigniew", "Zbigniewowski", LocalDate.now(), Person.GENDER.MALE, "00000000010");
	}

	Person zbigniewWithNonMaleId() {
		return new Person("Zbigniew", "Zbigniewowski", LocalDate.now(), Person.GENDER.MALE, "00000000020");
	}

}