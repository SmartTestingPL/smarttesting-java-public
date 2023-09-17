package pl.smarttesting.verifier.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja po imieniu. Dla kobiety imię musi się kończyc na "a".
 */
public class NameVerification implements Verification {
		
	private static final Logger log = LoggerFactory.getLogger(NameVerification.class);

	@Override
	public boolean passes(Person person) {
		boolean passed = true;
		if (person.getGender() == GENDER.FEMALE) {
			passed = person.getName().endsWith("a");
		}
		log.info("Person [{}] passed the name check [{}]", person, passed);
		return passed;
	}
}
