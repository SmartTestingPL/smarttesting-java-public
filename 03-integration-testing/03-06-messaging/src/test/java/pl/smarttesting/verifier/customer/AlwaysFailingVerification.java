package pl.smarttesting.verifier.customer;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja, która zawsze jest negatywna - klient chce nas oszukać.
 */
class AlwaysFailingVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		return false;
	}
}
