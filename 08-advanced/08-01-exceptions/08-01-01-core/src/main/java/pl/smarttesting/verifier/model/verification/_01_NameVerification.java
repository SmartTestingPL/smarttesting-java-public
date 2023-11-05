package pl.smarttesting.verifier.model.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.Verification;

/**
 * Weryfikacja po nazwisku.
 */
class _01_NameVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		System.out.println("Person's gender is [" + person.getGender().toString() + "]");
		if (person.getName() == null) {
			throw new NullPointerException("Name cannot be null.");
		}
		return !person.getName().equals("");
	}
}


