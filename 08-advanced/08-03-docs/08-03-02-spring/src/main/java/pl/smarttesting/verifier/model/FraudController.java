package pl.smarttesting.verifier.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kontroler, który dla żądania HTTP z metodą POST, w ciele którego
 * znajdzie się obiekt z klientem w postaci JSONa, zweryfikuje czy dana
 * osoba jest oszustem czy też nie.
 */
@RestController
class FraudController {

	private static final Logger log = LoggerFactory.getLogger(FraudController.class);

	private final CustomerVerifier customerVerifier;

	FraudController(CustomerVerifier customerVerifier) {
		this.customerVerifier = customerVerifier;
	}

	/**
	 * Metoda, która zostanie uruchomiona w momencie uzyskania odpowiedniego żądania HTTP.
	 * @param customer - zdeserializowany obiekt z formatu JSON
	 * @return status 200 dla osoby uczciwej, 401 dla oszusta
	 */
	@PostMapping(value = "/fraudCheck", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> fraudCheck(@RequestBody Customer customer) {
		log.info("Received a verification request for pl.smarttesting.customer [{}]", customer);
		CustomerVerificationResult result = customerVerifier.verify(customer)
				.stream()
				.map(r -> r.result)
				.allMatch(b -> b.equals(true)) ?
				CustomerVerificationResult.passed(customer.getUuid()) : CustomerVerificationResult.failed(customer.getUuid());
		if (result.getStatus() == CustomerVerificationResult.Status.VERIFICATION_FAILED) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return ResponseEntity.ok().build();
	}
}
