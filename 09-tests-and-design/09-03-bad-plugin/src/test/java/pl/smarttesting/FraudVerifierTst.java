package pl.smarttesting;

import org.junit.Assert;
import org.junit.Test;

/**
 * Klasa testowa wyłamująca się poza schemat nazewniczy testów.
 * Z IDE się wywali, z linii komend nie (jeśli w pluginie `maven-surefire-plugin`
 * nie umieścimy `failIfNoTests=true`).
 */
public class FraudVerifierTst {
	@Test
	public void should_mark_client_with_debt_as_fraud() {
		Assert.assertFalse(new FraudVerifier().isFraud(new Client(true)));
	}
}

class FraudVerifier {
	boolean isFraud(Client client) {
		return client.hasDebt;
	}
}

class Client {
	boolean hasDebt;

	Client(boolean hasDebt) {
		this.hasDebt = hasDebt;
	}
}