package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;

@Homework("Czy framework do mockowania działa?")
class SurnameVerificationTests {

	SurnameChecker checker = Mockito.mock(SurnameChecker.class);
	
	SurnameVerification verification = new SurnameVerification(checker);
	
	@Test
	void should_return_false_when_surname_invalid() {
		when(checker.checkSurname(any())).thenReturn(false);
		
		assertThat(verification.passes(person())).isFalse();
	}
	
	@Test
	void should_return_true_when_surname_invalid() {
		when(checker.checkSurname(any())).thenReturn(true);
		
		assertThat(verification.passes(person())).isTrue();
	}

	private Person person() {
		return new Person("a", "b", LocalDate.now(), GENDER.MALE, "1234567890");
	}

}
