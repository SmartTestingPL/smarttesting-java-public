package pl.smarttesting.verifier.model.verification;

import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.application.VerificationEvent;
import pl.smarttesting.verifier.model.Verification;
import pl.smarttesting.verifier.model.VerificationResult;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Weryfikacja po PESELu.
 */
class IdentificationNumberVerification implements Verification {

	private static final Logger log = LoggerFactory.getLogger(IdentificationNumberVerification.class);

	private final ApplicationEventPublisher eventPublisher;

	IdentificationNumberVerification(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public VerificationResult passes(Person person) {
		try {
			log.info("Running id verification");
			Thread.sleep(Math.abs(new Random().nextInt(2000)));
		}
		catch (InterruptedException ex) {
			throw new IllegalStateException(ex);
		}
		log.info("Id verification done");
		boolean result = genderMatchesIdentificationNumber(person)
				&& identificationNumberStartsWithDateOfBirth(person)
				&& identificationNumberWeightIsCorrect(person);
		eventPublisher.publishEvent(new VerificationEvent(this, "id", result));
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
