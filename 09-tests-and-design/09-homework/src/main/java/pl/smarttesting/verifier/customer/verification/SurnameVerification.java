package pl.smarttesting.verifier.customer.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.VerificationEvent;

/**
 * Weryfikacja wieku osoby wnioskującej o udzielenie pożyczki.
 */
public class SurnameVerification implements Verification {

	private final SurnameChecker surnameChecker;

	public SurnameVerification(SurnameChecker surnameChecker) {
		this.surnameChecker = surnameChecker;
	}

	@Override
	public boolean passes(Person person) {
		return surnameChecker.checkSurname(person);
	}
}

