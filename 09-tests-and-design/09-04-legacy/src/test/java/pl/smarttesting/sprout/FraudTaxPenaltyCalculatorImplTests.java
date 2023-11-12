package pl.smarttesting.sprout;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class FraudTaxPenaltyCalculatorImplTests {
	/**
	 * Test z wykorzystaniem sztucznej implementacji dostępu do bazy danych.
	 */
	@Test
	void should_calculate_the_tax_for_fraudowski() {
		int fraudowskiAmount = 100;
		DatabaseAccessorImpl accessor = new FakeDatabaseAccessorImpl(fraudowskiAmount);
		_05_FraudTaxPenaltyCalculatorImpl calculator = new _05_FraudTaxPenaltyCalculatorImpl(accessor);

		int tax = calculator.calculateFraudTax("Fraudowski");

		then(tax).isEqualTo(fraudowskiAmount * 100);
	}

	/**
	 * Test z wykorzystaniem sztucznej implementacji dostępu do bazy danych.
	 * Weryfikuje implementację z użyciem if / else.
	 */
	@Test
	void should_calculate_the_tax_for_fraudowski_with_if_else() {
		int fraudowskiAmount = 100;
		DatabaseAccessorImpl accessor = new FakeDatabaseAccessorImpl(fraudowskiAmount);
		_06_FraudTaxPenaltyCalculatorImplIfElse calculator = new _06_FraudTaxPenaltyCalculatorImplIfElse(accessor);

		int tax = calculator.calculateFraudTax("Fraudowski");

		then(tax).isEqualTo(fraudowskiAmount * 100 * 10);
	}

	/**
	 * Test z wykorzystaniem sztucznej implementacji dostępu do bazy danych.
	 * Weryfikuje implementację z użyciem klasy kiełkującej.
	 */
	@Test
	void should_calculate_the_tax_for_fraudowski_with_sprout() {
		int fraudowskiAmount = 100;
		DatabaseAccessorImpl accessor = new FakeDatabaseAccessorImpl(fraudowskiAmount);
		_07_FraudTaxPenaltyCalculatorImplSprout calculator = new _07_FraudTaxPenaltyCalculatorImplSprout(accessor);

		int tax = calculator.calculateFraudTax("Fraudowski");

		then(tax).isEqualTo(fraudowskiAmount * 100 * 20);
	}
}

/**
 * Kalkulator podatku dla oszustów. Nie mamy do niego testów.
 */
class _05_FraudTaxPenaltyCalculatorImpl {

	private final DatabaseAccessorImpl databaseImpl;

	_05_FraudTaxPenaltyCalculatorImpl(DatabaseAccessorImpl databaseImpl) {
		this.databaseImpl = databaseImpl;
	}

	int calculateFraudTax(String name) {
		Client client = databaseImpl.getClientByName(name);
		if (client.amount < 0) {
			// WARNING: Don't touch this
			// nobody knows why it should be -3 anymore
			// but nothing works if you change this
			return -3;
		}
		return calculateTax(client.amount);
	}

	private int calculateTax(int amount) {
		return amount * 100;
	}
}

/**
 * Nowa funkcja systemu - dodajemy kod do nieprzetestowanego kodu.
 */
class _06_FraudTaxPenaltyCalculatorImplIfElse {

	private final DatabaseAccessorImpl databaseImpl;

	_06_FraudTaxPenaltyCalculatorImplIfElse(DatabaseAccessorImpl databaseImpl) {
		this.databaseImpl = databaseImpl;
	}

	int calculateFraudTax(String name) {
		Client client = databaseImpl.getClientByName(name);
		if (client.amount < 0) {
			// WARNING: Don't touch this
			// nobody knows why it should be -3 anymore
			// but nothing works if you change this
			return -3;
		}
		int tax = calculateTax(client.amount);
		if (tax > 10) {
			return tax * 10;
		}
		return tax;
	}

	private int calculateTax(int amount) {
		return amount * 100;
	}
}

/**
 * Klasa kiełkowania (sprout). Wywołamy kod, który został przetestowany.
 * Piszemy go poprzez TDD.
 */
class _07_FraudTaxPenaltyCalculatorImplSprout {

	private final DatabaseAccessorImpl databaseImpl;

	_07_FraudTaxPenaltyCalculatorImplSprout(DatabaseAccessorImpl databaseImpl) {
		this.databaseImpl = databaseImpl;
	}

	int calculateFraudTax(String name) {
		Client client = databaseImpl.getClientByName(name);
		if (client.amount < 0) {
			// WARNING: Don't touch this
			// nobody knows why it should be -3 anymore
			// but nothing works if you change this
			return -3;
		}
		int tax = calculateTax(client.amount);
		// chcemy obliczyć specjalny podatek
		return new SpecialTaxCalculator(tax).calculate();
	}

	private int calculateTax(int amount) {
		return amount * 100;
	}
}

class DatabaseAccessorImpl {
	public Client getClientByName(String name) {
		return new Client(name, true, 100);
	}
}

class FakeDatabaseAccessorImpl extends DatabaseAccessorImpl {

	private final int amount;

	FakeDatabaseAccessorImpl(int amount) {
		this.amount = amount;
	}

	@Override
	public Client getClientByName(String name) {
		return new Client("Fraudowski", true, amount);
	}
}

class Client {
	final String name;
	final boolean hasDebt;
	final int amount;

	Client(String name, boolean hasDebt, int amount) {
		this.name = name;
		this.hasDebt = hasDebt;
		this.amount = amount;
	}
}
