package pl.smarttesting.fraudverifier.customer.verification;

import org.apache.commons.lang3.StringUtils;
import pl.smarttesting.fraudverifier.Verification;
import pl.smarttesting.fraudverifier.customer.Person;

/**
 * Weryfikacja po imieniu.
 */
public class NameVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		return StringUtils.isAlpha(person.getName());
	}
}

