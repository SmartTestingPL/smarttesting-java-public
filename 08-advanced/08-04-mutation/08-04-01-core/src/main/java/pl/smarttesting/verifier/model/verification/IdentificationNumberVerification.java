package pl.smarttesting.verifier.model.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.model.Verification;
import pl.smarttesting.verifier.model.VerificationResult;

/**
 * Weryfikacja po PESELu.
 */
class IdentificationNumberVerification implements Verification {

	@Override
	public VerificationResult passes(Person person) {
		boolean result = genderMatchesIdentificationNumber(person);
		return new VerificationResult("id", result);
	}

	private boolean genderMatchesIdentificationNumber(Person person) {
		if (Integer.parseInt(person.getNationalIdentificationNumber()
				.substring(9, 10)) % 2 == 0) {
			return Person.GENDER.FEMALE.equals(person.getGender());
		}
		else {
			return Person.GENDER.MALE.equals(person.getGender());
		}
	}
}
