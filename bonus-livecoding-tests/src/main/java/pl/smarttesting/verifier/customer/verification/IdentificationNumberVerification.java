package pl.smarttesting.verifier.customer.verification;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.VerificationEvent;

import java.time.format.DateTimeFormatter;

/**
 * Weryfikacja poprawności numeru PESEL.
 * Zob. https://pl.wikipedia.org/wiki/PESEL#Cyfra_kontrolna_i_sprawdzanie_poprawno.C5.9Bci_numeru
 */
public class IdentificationNumberVerification implements Verification {

	private final EventEmitter eventEmitter;

	public IdentificationNumberVerification(EventEmitter eventEmitter) {
		this.eventEmitter = eventEmitter;
	}

	@Override
	public boolean passes(Person person) {
		boolean passes = genderMatchesIdentificationNumber(person)
				&& identificationNumberStartsWithDateOfBirth(person)
				&& identificationNumberWeightIsCorrect(person);
		eventEmitter.emit(new VerificationEvent(passes));
		return passes;
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

	private boolean identificationNumberStartsWithDateOfBirth(Person person) {
		String dateOfBirthString = person.getDateOfBirth()
				.format(DateTimeFormatter.ofPattern("yyMMdd"));
		if (dateOfBirthString.charAt(0) == '0') {
			int monthNum = Integer.parseInt(dateOfBirthString.substring(2, 4));
			monthNum += 20;
			dateOfBirthString = dateOfBirthString
					.substring(0, 2) + monthNum + dateOfBirthString.substring(4, 6);
		}
		return dateOfBirthString
				.equals(person.getNationalIdentificationNumber().substring(0, 6));
	}

	private boolean identificationNumberWeightIsCorrect(Person person) {
		int[] weights = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

		if (person.getNationalIdentificationNumber().length() != 11) {
			return false;
		}

		int weightSum = 0;
		for (int i = 0; i < 10; i++) {
			weightSum += Integer.parseInt(person.getNationalIdentificationNumber()
					.substring(i, i + 1)) * weights[i];
		}

		int actualSum = (10 - weightSum % 10) % 10;

		int checkSum = Integer
				.parseInt(person.getNationalIdentificationNumber().substring(10, 11));

		return actualSum == checkSum;
	}
}
