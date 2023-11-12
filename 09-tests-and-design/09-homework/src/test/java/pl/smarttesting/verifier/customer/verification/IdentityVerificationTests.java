package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;

@Homework("Czy ten test weryfikuje kod produkcyjny?")
class IdentityVerificationTests {

	@Test
	void should_verify_negatively_for_an_invalid_identity_number() {
		IdentityVerification verification = new IdentityVerification();
		
		then(verification.passes(withInvalidPesel())).isFalse();
	}

	private Person withInvalidPesel() {
		return new Person("jan", "kowalski", LocalDate.now(), GENDER.MALE, "abcdefghijk");
	}

}
