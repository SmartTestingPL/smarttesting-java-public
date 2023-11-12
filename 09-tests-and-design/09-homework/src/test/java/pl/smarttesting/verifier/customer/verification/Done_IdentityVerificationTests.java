package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;

/**
 * Pierwotny test testuje tylko negatywny przypadek. Kod produkcyjny zawiera automatycznie generowany kod
 * przez IDE. Czasami zdarza się, że test przechodzi, a nie powinien tylko dlatego, że użyte
 * zostały wartości domyślne takie jak null, 0, false.
 */
class Done_IdentityVerificationTests {

	/**
	 * Test waliduje weryfikacje osoby z błędnym numerem PESEL. 
	 */
	@Test
	void should_verify_negatively_for_an_invalid_identity_number() {
		IdentityVerification verification = new IdentityVerification();
		
		then(verification.passes(withInvalidPesel())).isFalse();
	}
	
	/**
	 * Test waliduje weryfikacje osoby z poprawnym numerem PESEL.
	 * Wywali się, ponieważ kod produkcyjny zawiera tylko wartości domyślne. 
	 */
	@Test
	@Disabled("Wywali się, bo kod produkcyjny zawiera tylko wartości domyślne")
	void should_verify_positively_for_a_valid_identity_number() {
		IdentityVerification verification = new IdentityVerification();
		
		then(verification.passes(withValidPesel())).isTrue();
	}

	private Person withInvalidPesel() {
		return new Person("jan", "kowalski", LocalDate.now(), GENDER.MALE, "abcdefghijk");
	}
	
	private Person withValidPesel() {
		return new Person("jan", "kowalski", LocalDate.now(), GENDER.MALE, "49120966834");
	}

}
