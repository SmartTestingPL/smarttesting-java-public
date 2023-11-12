package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.verifier.EventEmitter;

@Homework("Czy ten test w ogóle coś testuje?")
class NameVerificationTests {

	@Test
	void should_verify_positively_when_name_is_alphanumeric() {
		NameVerification verification = new NameVerification(new EventEmitter());
		Person person = withValidName();
		boolean expected = verification.verify(person);
		
		then(verification.passes(person)).isEqualTo(expected);
	}
	
	@Test
	void should_verify_negatively_when_name_is_not_alphanumeric() {
		NameVerification verification = new NameVerification(new EventEmitter());
		Person person = withInvalidName();
		boolean expected = verification.verify(person);
		
		then(verification.passes(person)).isEqualTo(expected);
	}

	private Person withValidName() {
		return new Person("jan", "kowalski", LocalDate.now(), GENDER.MALE, "abcdefghijk");
	}
	
	private Person withInvalidName() {
		return new Person(null, null, LocalDate.now(), GENDER.MALE, "abcdefghijk");
	}

}
