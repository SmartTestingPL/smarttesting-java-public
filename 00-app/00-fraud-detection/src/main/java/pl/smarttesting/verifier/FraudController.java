package pl.smarttesting.verifier;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
	private final Counter fraudCheckVerificationFailureCounter;
	private final Counter fraudCheckVerificationSuccessCounter;

	FraudController(CustomerVerifier customerVerifier, MeterRegistry meterRegistry) {
		this.customerVerifier = customerVerifier;
		fraudCheckVerificationFailureCounter = Counter.builder("fraudcheck.results.failure")
			.register(meterRegistry);
		fraudCheckVerificationSuccessCounter = Counter.builder("fraudcheck.results.success")
			.register(meterRegistry);
	}

	/**
	 * Metoda, która zostanie uruchomiona w momencie uzyskania odpowiedniego żądania HTTP.
	 * @param customer - zdeserializowany obiekt z formatu JSON
	 * @return status 200 dla osoby uczciwej, 401 dla oszusta
	 */
	@PostMapping(value = "/fraudCheck", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> fraudCheck(@RequestBody Customer customer) {
		log.info("Received a verification request for customer [{}]", customer);
		CustomerVerificationResult result = customerVerifier.verify(customer);
		log.info("Verification for customer [{}] is [{}]", customer, result);
		if (result.getStatus() == CustomerVerificationResult.Status.VERIFICATION_FAILED) {
			fraudCheckVerificationFailureCounter.increment();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		fraudCheckVerificationSuccessCounter.increment();
		return ResponseEntity.ok().build();
	}
}
