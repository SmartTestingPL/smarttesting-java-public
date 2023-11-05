package pl.smarttesting.verifier.model;

import pl.smarttesting.customer.Person;

/**
 * Weryfikacja klienta.
 */
public interface Verification {

	/**
	 * Weryfikuje czy dana osoba nie jest oszustem.
	 * @param person - osoba do zweryfikowania
	 * @return {@link VerificationResult} rezultat weryfikacji
	 */
	VerificationResult passes(Person person);

	/**
	 * @return nazwa weryfikacji
	 */
	default String name() {
		return "name";
	}
}