package pl.smarttesting.verifier.model.verification;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.application.VerificationEvent;
import pl.smarttesting.verifier.model.Verification;
import pl.smarttesting.verifier.model.VerificationResult;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

/**
 * Weryfikacja po nazwisku.
 *
 * Po zakończonym procesowaniu weryfikacji wysyła zdarzenie z rezultatem weryfikacji.
 */
class NameVerification implements Verification {

	private static final Logger log = LoggerFactory.getLogger(NameVerification.class);

	private final ApplicationEventPublisher eventPublisher;

	NameVerification(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public VerificationResult passes(Person person) {
		try {
			log.info("Running name verification");
			// Symuluje procesowanie w losowym czasie do 2 sekund
			Thread.sleep(Math.abs(new Random().nextInt(2000)));
		}
		catch (InterruptedException ex) {
			throw new IllegalStateException(ex);
		}
		log.info("Name verification done");
		boolean result = StringUtils.hasText(person.getName());
		eventPublisher.publishEvent(new VerificationEvent(this, "name", result));;
		return new VerificationResult("name", result);
	}
}


