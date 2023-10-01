package pl.smarttesting.verifier;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Deweloperska wersja nasłuchiwacza na wiadomości. Dzięki adnotacji
 * {@link RestControllerEndpoint} wystawiamy endpoint RESTowy, dzięki któremu
 * za pomocą metody POST jesteśmy w stanie zasymulować uzyskanie wiadomości z kolejki.
 */
@RestControllerEndpoint(id = "fraud")
public class DevFraudListener implements FraudListener {

	private final FraudListener delegate;

	public DevFraudListener(FraudListener delegate) {
		this.delegate = delegate;
	}

	@Override
	@PostMapping
	public void onFraud(@RequestBody CustomerVerification customerVerification) {
		this.delegate.onFraud(customerVerification);
	}
}
