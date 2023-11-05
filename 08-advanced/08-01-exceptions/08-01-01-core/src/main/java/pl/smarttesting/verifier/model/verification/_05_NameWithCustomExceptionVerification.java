package pl.smarttesting.verifier.model.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.Verification;

/**
 * Weryfikacja po nazwisku rzucająca wyjątek w przypadku błędu.
 */
class _05_NameWithCustomExceptionVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		System.out.println("Person's gender is [" + person.getGender().toString() + "]");
		if (person.getName() == null) {
			throw new _04_VerificationException("Name cannot be null.");
		}
		return !person.getName().equals("");
	}
}

