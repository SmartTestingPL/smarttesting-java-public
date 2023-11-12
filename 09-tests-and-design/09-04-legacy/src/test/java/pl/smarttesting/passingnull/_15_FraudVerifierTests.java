package pl.smarttesting.passingnull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class _15_FraudVerifierTests {

	/**
	 * Przykład testu, gdzie zakładamy, że nie musimy tworzyć wszystkich obiektów
	 * i podmieniamy je nullem. Jeśli zależność jest wymagana - test nam się wywali.
	 */
	@Test
	void should_mark_client_with_debt_as_fraud() {
		_16_FraudVerifier verifier = new _16_FraudVerifier(null, null, new DatabaseAccessorImpl());

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	/**
	 * Przykład testu, gdzie zakładamy, że nie musimy tworzyć wszystkich obiektów
	 * i podmieniamy je nullem. Niestety nie trafiamy i leci nam NullPointerException,
	 * gdyż dani kolaboratorzy byli wymagani.
	 */
	@Disabled
	@Test
	void should_calculate_penalty_when_fraud_applies_for_a_loan() {
		_16_FraudVerifier verifier = new _16_FraudVerifier(new PenaltyCalculatorImpl(), null, null);

		long penalty = verifier.calculateFraudPenalty("Fraudowski");

		then(penalty).isGreaterThan(0L);
	}

	/**
	 * Wygląda na to, że musimy przekazać jeszcze {@link TaxHistoryRetrieverImpl}.
	 */
	@Test
	void should_calculate_penalty_when_fraud_applies_for_a_loan_with_both_deps() {
		_16_FraudVerifier verifier = new _16_FraudVerifier(new PenaltyCalculatorImpl(), new TaxHistoryRetrieverImpl(), null);

		long penalty = verifier.calculateFraudPenalty("Fraudowski");

		then(penalty).isGreaterThan(0L);
	}
}

/**
 * Implementacja zawierająca dużo zależności, skomplikowany, długi kod.
 */
class _16_FraudVerifier {

	private final PenaltyCalculatorImpl penalty;
	private final TaxHistoryRetrieverImpl history;
	private final DatabaseAccessorImpl accessor;

	_16_FraudVerifier(PenaltyCalculatorImpl penalty, TaxHistoryRetrieverImpl history, DatabaseAccessorImpl accessor) {
		this.penalty = penalty;
		this.history = history;
		this.accessor = accessor;
	}

	public long calculateFraudPenalty(String name) {
		// 5 000 linijek kodu dalej...

		// set client history to false, otherwise it won't work
		long lastRevenue = history.returnLastRevenue(new Client(name, false));
		// set client history to true, otherwise it won't work
		long penalty = this.penalty.calculatePenalty(new Client(name, true));
		return lastRevenue / 50 + penalty;
	}

	public boolean isFraud(String name) {
		// 7 000 linijek kodu dalej ...

		Client client = accessor.getClientByName(name);
		return client.hasDebt;
	}
}

class PenaltyCalculatorImpl {
	long calculatePenalty(Client client) {
		return 100L;
	}
}

class TaxHistoryRetrieverImpl {
	long returnLastRevenue(Client client) {
		return 150;
	}
}

class DatabaseAccessorImpl {
	public Client getClientByName(String name) {
		return new Client("Fraudowski", true);
	}
}

class Client {
	final String name;
	final boolean hasDebt;

	Client(String name, boolean hasDebt) {
		this.name = name;
		this.hasDebt = hasDebt;
	}
}