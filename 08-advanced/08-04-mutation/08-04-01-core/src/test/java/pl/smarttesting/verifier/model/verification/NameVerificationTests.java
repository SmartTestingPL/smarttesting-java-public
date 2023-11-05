package pl.smarttesting.verifier.model.verification;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.VerificationResult;

import static org.assertj.core.api.BDDAssertions.then;

class NameVerificationTests {

	@Test
	void should_return_positive_result_when_name_is_not_blank() {
		NameVerification verification = new NameVerification();

		VerificationResult result = verification.passes(namelessPerson());

		then(result.result).isFalse();
	}

	Person namelessPerson() {
		return new Person("", "Stefanowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}
}