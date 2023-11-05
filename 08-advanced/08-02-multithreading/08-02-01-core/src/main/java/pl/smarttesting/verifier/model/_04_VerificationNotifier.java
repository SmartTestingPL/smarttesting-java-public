package pl.smarttesting.verifier.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;

/**
 * Notyfikuje o znalezionych oszustach.
 */
public class _04_VerificationNotifier implements FraudAlertNotifier {

	private static final Logger log = LoggerFactory.getLogger(_04_VerificationNotifier.class);

	/**
	 * Dzięki adnotacji {@link Async} procesowanie będzie miało miejsce w osobnym wątku.
	 * 
	 * @param customerVerification - weryfikacja klienta
	 */
	@Async
	@Override
	public void fraudFound(CustomerVerification customerVerification) {
		try {
			log.info("Running fraud notification in a new thread");
			Thread.sleep(2000);
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
