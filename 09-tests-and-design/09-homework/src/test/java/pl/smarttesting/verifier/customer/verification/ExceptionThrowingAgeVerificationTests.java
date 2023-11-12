package pl.smarttesting.verifier.customer.verification;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;

@Homework("Czy na pewno te asercje są poprawne?")
class ExceptionThrowingAgeVerificationTests {

	ExceptionThrowingAgeVerification verification = new ExceptionThrowingAgeVerification();
	
	@Test
	void testGood() {
		Assertions.assertThat(verification.passes(goodPerson()));
	}
	
	@Test
	void testBad() {
		Assertions.assertThatThrownBy(() -> verification.passes(badPerson()));
	}
	
	private Person goodPerson() {
		return new Person("A", "B", LocalDate.now().minusYears(20), GENDER.FEMALE, "34567890");
	}
	
	private Person badPerson() {
		return new Person("A", "B", LocalDate.now(), GENDER.FEMALE, "34567890");
	}

}
