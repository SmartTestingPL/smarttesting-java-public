package pl.smarttesting.verifier;

import pl.smarttesting.customer.Person;

/**
 * Weryfikacja klienta.
 */
public interface Verification {

	/**
	 * Weryfikuje czy dana osoba nie jest oszustem.
	 * @param person - osoba do zweryfikowania
	 * @return {@code false} dla oszusta.
	 */
	default boolean passes(Person person) {
		return false;
	};
}
