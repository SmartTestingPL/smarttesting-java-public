package pl.smarttesting.verifier.verification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja po wieku. Osoba w wieku powyżej 18 lat zostanie
 * zweryfikowana pozytywnie.
 */
public class AgeVerification implements Verification {
	
	private static final Logger log = LoggerFactory.getLogger(AgeVerification.class);

	@Override
	public boolean passes(Person person) {
		if (person.getAge() < 0) {
			throw new IllegalStateException("Age cannot be negative.");
		}
		boolean passed = person.getAge() >= 16 && person.getAge() <= 99;
		log.info("Person [{}] passed the age check [{}]", person, passed);
		return passed;
	}
}
