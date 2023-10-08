package pl.smarttesting.fraudverifier.customer.verification;

import pl.smarttesting.fraudverifier.Verification;
import pl.smarttesting.fraudverifier.customer.Person;

/**
 * Weryfikacja po wieku.
 *
 */
public class AgeVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		if (person.getAge() < 0) {
			throw new IllegalStateException("Age cannot be negative.");
		}
		return person.getAge() >= 18 && person.getAge() <= 99;
	}
}

