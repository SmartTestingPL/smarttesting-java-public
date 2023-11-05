package pl.smarttesting.verifier.model;

import pl.smarttesting.customer.Person;

/**
 * Weryfikacja klienta.
 */
public interface Verification {

	/**
	 * Weryfikuje czy dana osoba nie jest oszustem.
	 * @return {@code false} dla oszusta.
	 */
	boolean passes(Person person);
}
