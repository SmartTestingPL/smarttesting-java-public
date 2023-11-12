package pl.smarttesting.verifier.customer.verification;

import org.apache.commons.lang3.StringUtils;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.VerificationEvent;

/**
 * Weryfikacja po imieniu.
 *
 */
public class NameVerification implements Verification {

	private final EventEmitter eventEmitter;

	public NameVerification(EventEmitter eventEmitter) {
		this.eventEmitter = eventEmitter;
	}

	@Override
	public boolean passes(Person person) {
		boolean passes = verify(person);
		eventEmitter.emit(new VerificationEvent(passes));
		return passes;
	}

	boolean verify(Person person) {
		return StringUtils.isAlpha(person.getName());
	}
}

