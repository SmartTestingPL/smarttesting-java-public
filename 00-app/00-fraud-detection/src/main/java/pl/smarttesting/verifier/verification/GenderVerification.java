package pl.smarttesting.verifier.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja po płci. Płeć musi być wybrana.
 */
public class GenderVerification implements Verification {
	
	private static final Logger log = LoggerFactory.getLogger(GenderVerification.class);

	@Override
	public boolean passes(Person person) {
		boolean passed = person.getGender() != null;
		log.info("Person [{}] passed the gender check [{}]", person, passed);
		return passed;
	}
}
