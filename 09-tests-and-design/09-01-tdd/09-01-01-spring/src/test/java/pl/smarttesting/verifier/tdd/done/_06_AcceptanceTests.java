package pl.smarttesting.verifier.tdd.done;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = _06_AcceptanceTests.Config.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class _06_AcceptanceTests {

	@LocalServerPort
	int port;

	@Test
	void should_verify_a_client_with_debt_as_fraud() {
		Client fraud = clientWithDebt();

		ResponseEntity<VerificationResult> verification = verifyClient(fraud);

		thenIsVerifiedAsFraud(verification);
	}

	private Client clientWithDebt() {
		return new Client(true);
	}

	private ResponseEntity<VerificationResult> verifyClient(Client client) {
		return new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(client), VerificationResult.class);
	}

	private void thenIsVerifiedAsFraud(ResponseEntity<VerificationResult> verification) {
		then(verification).isNotNull();
		then(verification.getBody())
				.isNotNull()
				.extracting("status")
				.isEqualTo(VerificationStatus.FRAUD);
	}

	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	static class Config {
		@Bean
		FraudController controller(FraudVerifier fraudVerifier) {
			return new FraudController(fraudVerifier);
		}

		@Bean
		FraudVerifier fraudService() {
			return new FraudVerifier();
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

		private final FraudVerifier fraudVerifier;

		FraudController(FraudVerifier fraudVerifier) {
			this.fraudVerifier = fraudVerifier;
		}

		@PostMapping("/fraudCheck")
		VerificationResult fraud(@RequestBody Client client) {
			return this.fraudVerifier.verify(client);
		}
	}

	static class FraudVerifier {

		VerificationResult verify(Client client) {
			if (client.hasDebt) {
				return new VerificationResult(VerificationStatus.FRAUD);
			}
			return new VerificationResult(VerificationStatus.NOT_FRAUD);
		}
	}
}
