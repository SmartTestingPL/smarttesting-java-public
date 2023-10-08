package pl.smarttesting.fraudverifier;

import pl.smarttesting.fraudverifier.customer.Person;

/**
 * Weryfikacja klienta.
 */
public interface Verification {

	boolean passes(Person person);
}
