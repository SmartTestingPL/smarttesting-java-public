package pl.smarttesting.verifier.tdd;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = _02_AcceptanceControllerTests.Config.class,
		webEnvironment = RANDOM_PORT)
class _02_AcceptanceControllerTests {

	@LocalServerPort
	int port;

	@Disabled
	@Test
	void should_verify_a_client_with_debt_as_fraud() {
		Client fraud = clientWithDebt();

		ResponseEntity<VerificationResult> verification = verifyClient(fraud);

		thenIsVerifiedAsFraud(verification);
	}

	@Disabled
	@Test
	void should_verify_a_client_with_debt_as_fraud_with_better_message() {
		Client fraud = clientWithDebt();

		ResponseEntity<VerificationResult> verification = verifyClient(fraud);

		thenIsVerifiedAsFraudWithBetterMessage(verification);
	}

	private Client clientWithDebt() {
		return new Client(true);
	}

	private ResponseEntity<VerificationResult> verifyClient(Client client) {
		return sendAnHttpJsonToFraudCheck(client);
	}

	private void thenIsVerifiedAsFraud(ResponseEntity<VerificationResult> verification) {
		then(verification).isNotNull();
		then(verification.getBody())
				.isNotNull()
				.extracting("status")
				.isEqualTo(VerificationStatus.FRAUD);
	}

	private void thenIsVerifiedAsFraudWithBetterMessage(ResponseEntity<VerificationResult> verification) {
		then(verification).isNotNull();
		then(verification.getBody())
				.as("Response from Fraud Service")
				.isNotNull()
				.extracting("status")
				.as("Verification Status")
				.isEqualTo(VerificationStatus.FRAUD);
	}

	private ResponseEntity<VerificationResult> sendAnHttpJsonToFraudCheck(Client client) {
		return new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/fraudCheck"))
				.contentType(APPLICATION_JSON)
				.body(client), VerificationResult.class);
	}
	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	static class Config {

		@Bean
		FraudController controller() {
			return new FraudController();
		}
	}
	@JsonAutoDetect(fieldVisibility = ANY)
	static class Client {

		final boolean hasDebt;

		@JsonCreator
		Client(@JsonProperty("hasDebt") boolean hasDebt) {
			this.hasDebt = hasDebt;
		}

	}

	@JsonAutoDetect(fieldVisibility = ANY)
	static class VerificationResult {
		final VerificationStatus status;

		@JsonCreator
		VerificationResult(@JsonProperty("status") VerificationStatus status) {
			this.status = status;
		}
	}

	enum VerificationStatus {
		FRAUD, NOT_FRAUD
	}

	@RestController
	static class FraudController {
		@PostMapping("/fraudCheck")
		VerificationResult fraud(@RequestBody Client client) {
			return null;
		}
	}
}
