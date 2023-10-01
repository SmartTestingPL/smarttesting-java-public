package pl.smarttesting.verifier.customer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Wysyłacz wiadomości, który wrzuca wiadomości na kolejkę w pamięci.
 */
class InMemoryMessaging implements FraudAlertNotifier {

	BlockingQueue<CustomerVerification> broker = new LinkedBlockingQueue<>(20);

	@Override
	public void fraudFound(CustomerVerification customerVerification) {
		broker.add(customerVerification);
	}

	/**
	 * Pozwala na wyjęcie weryfikacji z kolejki.
	 * @return ostatni element wrzucony na kolejkę lub {@code null} jeśli brak
	 */
	CustomerVerification poll() {
		try {
			return broker.poll(100, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
