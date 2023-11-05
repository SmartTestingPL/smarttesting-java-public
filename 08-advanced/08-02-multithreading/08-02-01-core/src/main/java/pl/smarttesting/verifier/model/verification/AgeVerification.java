package pl.smarttesting.verifier.model.verification;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.application.VerificationEvent;
import pl.smarttesting.verifier.model.Verification;
import pl.smarttesting.verifier.model.VerificationResult;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Weryfikacja po wieku.
 *
 * Po zakończonym procesowaniu weryfikacji wysyła zdarzenie z rezultatem weryfikacji.
 */
class AgeVerification implements Verification {

	private static final Logger log = LoggerFactory.getLogger(AgeVerification.class);

	private final ApplicationEventPublisher eventPublisher;

	AgeVerification(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public VerificationResult passes(Person person) {
		try {
			log.info("Running age verification");
			// Symuluje procesowanie w losowym czasie do 2 sekund
			Thread.sleep(Math.abs(new Random().nextInt(2000)));
		}
		catch (InterruptedException ex) {
			throw new IllegalStateException(ex);
		}
		if (person.getAge() < 0) {
			log.warn("Age is negative");
			throw new IllegalStateException("Age cannot be negative.");
		}
		log.info("Age verification done");
		boolean result = person.getAge() >= 18 && person.getAge() <= 99;
		eventPublisher.publishEvent(new VerificationEvent(this, "age", result));
		return new VerificationResult("age", result);
	}
}


