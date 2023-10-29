package pl.smarttesting.loanorders.fraud;

import pl.smarttesting.loanorders.customer.Customer;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Klient do komunikacji z serwisem Fraud.
 */
@Component
public class FraudWebClient {

	private final RestTemplate restTemplate;

	public FraudWebClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public ResponseEntity<CustomerVerificationResult> verifyCustomer(Customer customer) {
		return restTemplate.postForEntity("http://fraud-verifier/customers",
				customer, CustomerVerificationResult.class);
	}
}
