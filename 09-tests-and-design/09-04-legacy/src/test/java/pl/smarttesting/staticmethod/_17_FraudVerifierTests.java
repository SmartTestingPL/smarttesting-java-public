package pl.smarttesting.staticmethod;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class _17_FraudVerifierTests {

	/**
	 * Test się wywala, gdyż wywołanie `isFraud` wywoła połączenie do bazy danych.
	 * Nie wierzysz? Zakomentuj @Disabled i sprawdź sam!
	 */
	@Disabled
	@Test
	void should_mark_client_with_debt_as_fraud() {
		_18_FraudVerifier verifier = new _18_FraudVerifier();

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	/**
	 * Test wykorzystujący możliwość podmiany globalnej, statycznej instancji produkcyjnej
	 * na wersję testową, która zwraca wartość ustawioną na sztywno.
	 */
	@Test
	void should_mark_client_with_debt_as_fraud_with_static() {
		_20_DatabaseAccessorImplWithSetter.setInstance(new _21_FakeDatabaseAccessor());

		FraudVerifierForSetter verifier = new FraudVerifierForSetter();

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	/**
	 * Ważne, żeby po sobie posprzątać!
	 */
	@AfterEach
	void clean() {
		_20_DatabaseAccessorImplWithSetter.reset();
	}

}

/**
 * Przykład implementacji wołającej singleton {@link _19_DatabaseAccessorImpl}.
 */
class _18_FraudVerifier {

	boolean isFraud(String name) {
		Client client = _19_DatabaseAccessorImpl.getInstance().getClientByName(name);
		return client.hasDebt;
	}
}

class FraudVerifierForSetter {

	boolean isFraud(String name) {
		Client client = _20_DatabaseAccessorImplWithSetter.getInstance().getClientByName(name);
		return client.hasDebt;
	}
}

class _19_DatabaseAccessorImpl {

	private static _19_DatabaseAccessorImpl instance = new _19_DatabaseAccessorImpl();

	protected _19_DatabaseAccessorImpl() {

	}

	public static _19_DatabaseAccessorImpl getInstance() {
		return instance;
	}

	public Client getClientByName(String name) {
		Client client =  performLongRunningTask(name);
		System.out.println(client.name);
		doSomeAdditionalWork(client);
		return client;
	}

	private static void doSomeAdditionalWork(Client client) {
		System.out.println("Additional work done");
	}

	private static Client performLongRunningTask(String name) {
		throw new IllegalStateException("Can't connect to the database");
	}
}

class _21_FakeDatabaseAccessor extends _20_DatabaseAccessorImplWithSetter {
	@Override
	public Client getClientByName(String name) {
		return new Client("Fraudowski", true);
	}
}

class _20_DatabaseAccessorImplWithSetter {

	private static _20_DatabaseAccessorImplWithSetter instance = new _20_DatabaseAccessorImplWithSetter();

	protected _20_DatabaseAccessorImplWithSetter() {

	}

	public static _20_DatabaseAccessorImplWithSetter getInstance() {
		return instance;
	}

	/**
	 * Ustawiamy wartość testową.
	 */
	public static void setInstance(_20_DatabaseAccessorImplWithSetter databaseAccessorImplWithSetter) {
		instance = databaseAccessorImplWithSetter;
	}

	/**
	 * Resetujemy wartość do wartości produkcyjnej.
	 */
	public static void reset() {
		instance = new _20_DatabaseAccessorImplWithSetter();
	}

	public Client getClientByName(String name) {
		Client client =  performLongRunningTask(name);
		System.out.println(client.name);
		doSomeAdditionalWork(client);
		return client;
	}

	private static void doSomeAdditionalWork(Client client) {
		System.out.println("Additional work done");
	}

	private static Client performLongRunningTask(String name) {
		throw new IllegalStateException("Can't connect to the database");
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