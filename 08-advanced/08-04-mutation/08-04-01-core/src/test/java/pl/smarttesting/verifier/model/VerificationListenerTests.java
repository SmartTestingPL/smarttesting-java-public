package pl.smarttesting.verifier.model;

import org.junit.jupiter.api.Test;
import pl.smarttesting.verifier.application.VerificationEvent;

import static org.assertj.core.api.BDDAssertions.then;

class VerificationListenerTests {

	@Test
	void should_add_event() {
		VerificationListener listener = new VerificationListener();
		VerificationEvent event = new VerificationEvent(this, "age", true);

		listener.listen(event);

		then(listener.events).containsExactly(event);
	}

}