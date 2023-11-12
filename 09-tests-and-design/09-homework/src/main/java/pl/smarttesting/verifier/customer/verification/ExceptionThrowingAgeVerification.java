package pl.smarttesting.verifier.customer.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja po wieku - jeśli nie przechodzi to leci wyjątek.
 *
 */
public class ExceptionThrowingAgeVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		boolean passes = person.getAge() >= 18;
		if (!passes) {
			throw new IllegalArgumentException("You cannot be below 18 years of age!");
		}
		return passes;
	}
}

