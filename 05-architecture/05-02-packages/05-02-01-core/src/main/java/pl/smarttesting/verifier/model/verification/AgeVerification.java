package pl.smarttesting.verifier.model.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.Verification;

/**
 * Weryfikacja po wieku. Osoba w odpowiednim wieku zostanie
 * zweryfikowana pozytywnie.
 */
class AgeVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		if (person.getAge() < 0) {
			throw new IllegalStateException("Age cannot be negative.");
		}
		return person.getAge() >= 18 && person.getAge() <= 99;
	}
}
