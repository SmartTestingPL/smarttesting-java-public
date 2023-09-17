package pl.smarttesting.verifier.customer;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test zawiera przykład `false pass`.
 */
class SimpleVerificationTest {


	// Przykład `false pass`. Test weryfikuje nie to co trzeba (jakieś pole na obiekcie, zamiast zwracanej wartości),
	// przez co przechodzi, mimo że właściwa implementacja nie została dodana.
	@Test
	void shouldFailSimpleVerificationFalsePass() {
		// Given
		SimpleVerification verification = new SimpleVerification();
		Person person = new Person("John", "Smith",
				LocalDate.of(1996, 8, 28), Person.GENDER.MALE,
				"96082812079");

		// When
		verification.passes(person);

		// Then
		assertThat(verification.verificationPassed()).isTrue();
	}

}