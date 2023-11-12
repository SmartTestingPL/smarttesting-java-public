package pl.smarttesting.verifier.customer.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.VerificationEvent;

/**
 * Weryfikacja po warunkach biznesowych. Chyba ta klasa robi za dużo, no
 * ale trudno...
 *
 */
public class BusinessRulesVerification implements Verification {

	private final EventEmitter eventEmitter;
	private final VerifierManagerImpl verifier;

	public BusinessRulesVerification(EventEmitter eventEmitter, VerifierManagerImpl verifier) {
		this.eventEmitter = eventEmitter;
		this.verifier = verifier;
	}

	@Override
	public boolean passes(Person person) {
 		boolean passes = verifier.verifyName(person);
		passes = passes && verifier.verifyAddress(person);
		passes = passes && verifier.verifyPhone(person);
		passes = passes && verifier.verifySurname(person);
		passes = passes && verifier.verifyTaxInformation(person);
		eventEmitter.emit(new VerificationEvent(passes));
		return passes;
	}
}

