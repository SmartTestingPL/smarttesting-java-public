package pl.smarttesting.verifier.model.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.Verification;
import pl.smarttesting.verifier.model.VerificationResult;

/**
 * Weryfikacja po wieku. Osoba w odpowiednim wieku zostanie
 * zweryfikowana pozytywnie.
 */
class _01_AgeVerification implements Verification {

	private static final Logger log = LoggerFactory.getLogger(_01_AgeVerification.class);

	@Override
	public VerificationResult passes(Person person) {
		int age = person.getAge();
		if (age < 0) {
			log.warn("Age is negative");
			throw new IllegalStateException("Age cannot be negative.");
		}
		log.info("Person has age [{}]", age);
		boolean result = age >= 18 && age <= 99;
		return new VerificationResult("age", result);
	}
}


