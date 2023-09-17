package pl.smarttesting.verifier;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;

import org.springframework.web.client.RestTemplate;


/**
 * Klient do komunikacji z Biurem Informacji Kredytowej po HTTP.
 */
public class BIKVerificationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(BIKVerificationService.class);

	private final String bikServiceUri;

	private final RestTemplate restTemplate;

	public BIKVerificationService(String bikServiceUri, RestTemplate restTemplate) {
		this.bikServiceUri = uriWithSlash(bikServiceUri);
		this.restTemplate = restTemplate;
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
			LOG.info("Will send a request to Fraud Detection");
			Map<String, Object> result = restTemplate.getForObject(URI.create(bikServiceUri + customer.getPerson()
							.getNationalIdentificationNumber()), Map.class);
			if (CustomerVerificationResult.Status.VERIFICATION_PASSED.name()
					.equals(result.get("status"))) {
				return CustomerVerificationResult.passed(customer.getUuid());
			}
		}
		catch (Exception exception) {
			LOG.error("Http request execution failed.", exception);
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
	
	private String uriWithSlash(String bikServiceUri) {
		return bikServiceUri.endsWith("/") ? bikServiceUri : bikServiceUri + "/";
	}

}
