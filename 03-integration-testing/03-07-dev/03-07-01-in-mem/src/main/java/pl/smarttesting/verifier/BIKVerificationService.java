package pl.smarttesting.verifier;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;


/**
 * Klient do komunikacji z Biurem Informacji Kredytowej po HTTP.
 */
public class BIKVerificationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(BIKVerificationService.class);

	private final String bikServiceUri;

	private final HttpClient client;

	public BIKVerificationService(String bikServiceUri) {
		this.bikServiceUri = bikServiceUri;
		this.client = HttpClientBuilder.create().build();
	}

	/**
	 * Weryfikuje czy dana osoba jest oszustem poprzez
	 * wysłanie zapytania po HTTP do BIK. Do wykonania zapytania po HTTP wykorzystujemy
	 * bibliotekę Apache HTTP Client.
	 *
	 * @param customer - klient do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	public CustomerVerificationResult verify(Customer customer) {
		try {
			HttpResponse response = client
					.execute(new HttpGet(URI.create(bikServiceUri + customer.getPerson()
							.getNationalIdentificationNumber())));
			String externalStatus = EntityUtils.toString(response.getEntity());

			if (CustomerVerificationResult.Status.VERIFICATION_PASSED.name()
					.equals(externalStatus)) {
				return CustomerVerificationResult.passed(customer.getUuid());
			}
		}
		catch (IOException exception) {
			LOG.error("Http request execution failed.", exception);
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}

}
