package pl.smarttesting.verifier.customer.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.VerificationEvent;

/**
 * Weryfikacja wieku osoby wnioskującej o udzielenie pożyczki.
 */
public class AgeVerification implements Verification {

	private final EventEmitter eventEmitter;

	public AgeVerification(EventEmitter eventEmitter) {
		this.eventEmitter = eventEmitter;
	}

	@Override
	public boolean passes(Person person) {
		if (person.getAge() <= 0) {
			eventEmitter.emit(new VerificationEvent(false));
			throw new IllegalStateException("Age cannot be negative.");
		}
		boolean passes = person.getAge() >= 18 && person.getAge() <= 99;
		eventEmitter.emit(new VerificationEvent(passes));
		return passes;
	}
}

