package pl.smarttesting.verifier.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.verifier.application.VerificationEvent;

import org.springframework.context.event.EventListener;

/**
 * Nasłuchiwacz na zdarzenia weryfikacyjne. Zapisuje je w kolejce.
 */
public class VerificationListener {

	private static final Logger log = LoggerFactory.getLogger(VerificationListener.class);

	final BlockingQueue<VerificationEvent> events = new LinkedBlockingQueue<>();

	/**
	 * Metoda uruchomi się w momencie uzyskania zdarzenia typu {@link VerificationEvent}.
	 * @param event - zdarzenie do obsłużenia
	 */
	@EventListener(VerificationEvent.class)
	public void listen(VerificationEvent event) {
		log.info("Got an event! [{}]", event);
		events.add(event);
	}
}
