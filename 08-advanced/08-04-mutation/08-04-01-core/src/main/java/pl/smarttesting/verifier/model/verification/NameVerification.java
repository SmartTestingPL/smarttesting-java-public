package pl.smarttesting.verifier.model.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.Verification;
import pl.smarttesting.verifier.model.VerificationResult;

import org.springframework.util.StringUtils;

/**
 * Weryfikacja po nazwisku.
 */
class NameVerification implements Verification {

	@Override
	public VerificationResult passes(Person person) {
		boolean result = StringUtils.hasText(person.getName());
		return new VerificationResult("name", result);
	}
}


