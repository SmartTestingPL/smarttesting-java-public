package pl.smarttesting.verifier.verification;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja po nazwisku. Nazwisko musi mieć przynajmniej jedną samogłoskę.
 */
public class SurnameVerification implements Verification {
	
	private static final Logger log = LoggerFactory.getLogger(SurnameVerification.class);

	private static final String[] VOWELS = new String[] { "a", "i", "o", "u" };

	@Override
	public boolean passes(Person person) {
		boolean passed = Arrays.stream(VOWELS).anyMatch(v -> person.getSurname().toLowerCase().contains(v));
		log.info("Person [{}] passed the surname check [{}]", person, passed);
		return passed;
	}
}
