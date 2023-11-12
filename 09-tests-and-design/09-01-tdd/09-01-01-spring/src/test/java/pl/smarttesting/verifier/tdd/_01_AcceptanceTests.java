package pl.smarttesting.verifier.tdd;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Kod ze slajdów [Zacznijmy od testu].
 */
class _01_AcceptanceTests {
	@Disabled
	@Test
	void should_verify_a_client_with_debt_as_fraud() {
		Object fraud = clientWithDebt();

		Object verification = verifyClient(fraud);

		thenIsVerifiedAsFraud(verification);
	}

	private Object clientWithDebt() {
		return null;
	}

	private Object verifyClient(Object client) {
		return null;
	}

	private void thenIsVerifiedAsFraud(Object verification) {
		then(verification).isNotNull();
	}
}