package pl.smarttesting.verifier;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * Wersja deweloperska wysyłacza wiadomości. Zamiast brokera, mamy kolejkę
 * w pamięci.
 *
 * Poprzez adnotację {@link Endpoint} oraz {@link ReadOperation} wystawiamy
 * dodatkowo endpoint restowy z metodą GET w celu wylistowania zapisanych
 * elementów w kolejce.
 */
@Endpoint(id = "fraudalert")
class DevFraudAlertNotifier implements FraudAlertNotifier {

	BlockingQueue<CustomerVerification> broker = new LinkedBlockingQueue<>(50);

	@Override
	public void fraudFound(CustomerVerification customerVerification) {
		broker.add(customerVerification);
	}

	@ReadOperation
	public CustomerVerification[] poll() {
		return broker.toArray(new CustomerVerification[0]);
	}
}