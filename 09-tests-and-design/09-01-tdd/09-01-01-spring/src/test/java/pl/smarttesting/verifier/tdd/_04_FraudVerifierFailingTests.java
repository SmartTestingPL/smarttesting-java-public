package pl.smarttesting.verifier.tdd;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static org.assertj.core.api.BDDAssertions.then;
import static pl.smarttesting.verifier.tdd._04_FraudVerifierFailingTests.VerificationStatus.FRAUD;

public class _04_FraudVerifierFailingTests {
	@Disabled
	@Test
	void should_return_fraud_when_client_has_debt() {
		FraudVerifier verifier = new FraudVerifier();

		VerificationResult result = verifier.verify(clientWithDebt());

		then(result).as("Verification Result").isNotNull();
		then(result.status).isEqualTo(FRAUD);
	}

	@Disabled
	@Test
	void should_return_not_fraud_when_client_has_no_debt() {
		FraudVerifier verifier = new FraudVerifier();

		VerificationResult result = verifier.verify(clientWithoutDebt());

		then(result).as("Verification Result").isNotNull();
		then(result.status).isEqualTo(VerificationStatus.NOT_FRAUD);
	}

	private Client clientWithDebt() {
		return new Client(true);
	}

	private Client clientWithoutDebt() {
		return new Client(false);
	}

	static class FraudVerifier {

		VerificationResult verify(Client client) {
			return null;
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
}
