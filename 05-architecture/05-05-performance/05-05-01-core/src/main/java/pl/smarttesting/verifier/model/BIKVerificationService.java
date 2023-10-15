package pl.smarttesting.verifier.model;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;

/**
 * Klient do komunikacji z Biurem Informacji Kredytowej.
 */
class BIKVerificationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(BIKVerificationService.class);

	private final String bikServiceUri;

	private final HttpClient client;

	public BIKVerificationService(String bikServiceUri) {
		this.bikServiceUri = bikServiceUri;
		this.client = HttpClientBuilder.create().build();
	}

	/**
	 * Weryfikuje czy dana osoba jest oszustem. W wersji do tego modułu zwraca wcześniej
	 * przygotowane wartości.
	 *
	 * @param customer - klient do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	public CustomerVerificationResult verify(Customer customer) {
		try {
			Thread.sleep(300);
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		catch (InterruptedException exception) {
			LOG.error("Http request execution failed.", exception);
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}

}
