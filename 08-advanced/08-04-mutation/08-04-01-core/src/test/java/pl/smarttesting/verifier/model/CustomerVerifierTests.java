package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

class CustomerVerifierTests {

	@Test
	void should_collect_verification_results() {
		CustomerVerifier verifier = new CustomerVerifier(new HashSet<>(Arrays.asList(new FirstVerification(), new SecondVerification())));

		List<VerificationResult> verificationResults = verifier.verify(new Customer(UUID.randomUUID(), tooYoungStefan()));

		BDDAssertions.then(verificationResults)
				.containsOnly(
						new VerificationResult("first", false),
						new VerificationResult("second", true));
	}

	Person tooYoungStefan() {
		return new Person("Stefan", "Stefanowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}

}

class FirstVerification implements Verification {

	@Override
	public VerificationResult passes(Person person) {
		return new VerificationResult(name(), false);
	}

	@Override
	public String name() {
		return "first";
	}
}

class SecondVerification implements Verification {

	@Override
	public VerificationResult passes(Person person) {
		return new VerificationResult(name(), true);
	}

	@Override
	public String name() {
		return "second";
	}
}