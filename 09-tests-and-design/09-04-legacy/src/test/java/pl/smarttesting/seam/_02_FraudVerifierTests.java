package pl.smarttesting.seam;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class _02_FraudVerifierTests {
	/**
	 * Przykład próby napisania testu do istniejącej klasy łączącej się z bazą danych.
	 */
	@Disabled
	@Test
	void should_mark_client_with_debt_as_fraud() {
		_03_DatabaseAccessorImpl accessor = new _03_DatabaseAccessorImpl();
		FraudVerifier verifier = new FraudVerifier(accessor);

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	/**
	 * Przykład testu z wykorzystaniem szwa (seam).
	 */
	@Test
	void should_mark_client_with_debt_as_fraud_with_seam() {
		_03_DatabaseAccessorImpl accessor = new _04_FakeDatabaseAccessor();
		FraudVerifier verifier = new FraudVerifier(accessor);

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	@Disabled
	@Test
	void should_mark_client_with_debt_as_fraud_with_seam_logic_in_constructor() {
		_09_FraudVerifierLogicInConstructor verifier = new _09_FraudVerifierLogicInConstructor();

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	@Disabled
	@Test
	void should_create_an_instance_of_fraud_verifier() {
		new _09_FraudVerifierLogicInConstructor();
	}

	@Test
	void should_mark_client_with_debt_as_fraud_with_a_mock() {
		_10_DatabaseAccessorImplWithLogicInTheConstructor _12_DatabaseAccessor = mock(_10_DatabaseAccessorImplWithLogicInTheConstructor.class);
		given(_12_DatabaseAccessor.getClientByName(BDDMockito.anyString())).willReturn(new Client("Fraudowski", true));
		_11_FraudVerifierLogicInConstructorExtractLogic verifier = new _11_FraudVerifierLogicInConstructorExtractLogic(_12_DatabaseAccessor);

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	@Test
	void should_mark_client_with_debt_as_fraud_with_an_extracted_interface() {
		_13_FraudVerifierWithInterface verifier = new _13_FraudVerifierWithInterface(new _14_FakeDatabaseAccessorWithInterface());

		then(verifier.isFraud("Fraudowski")).isTrue();
	}

	@Test
	void should_mark_client_with_debt_as_fraud_with_seam_interface() {
		_12_DatabaseAccessor accessor = new _14_FakeDatabaseAccessorWithInterface();
		_13_FraudVerifierWithInterface verifier = new _13_FraudVerifierWithInterface(accessor);

		then(verifier.isFraud("Fraudowski")).isTrue();
	}
}

class FraudVerifier {

	private final _03_DatabaseAccessorImpl impl;

	FraudVerifier(_03_DatabaseAccessorImpl impl) {
		this.impl = impl;
	}

	boolean isFraud(String name) {
		Client client = impl.getClientByName(name);
		return client.hasDebt;
	}
}

class _09_FraudVerifierLogicInConstructor {

	private final _10_DatabaseAccessorImplWithLogicInTheConstructor impl;

	_09_FraudVerifierLogicInConstructor() {
		this.impl = new _10_DatabaseAccessorImplWithLogicInTheConstructor();
	}

	boolean isFraud(String name) {
		Client client = impl.getClientByName(name);
		return client.hasDebt;
	}
}

class _11_FraudVerifierLogicInConstructorExtractLogic {

	private final _10_DatabaseAccessorImplWithLogicInTheConstructor impl;

	_11_FraudVerifierLogicInConstructorExtractLogic() {
		this.impl = new _10_DatabaseAccessorImplWithLogicInTheConstructor();
	}

	_11_FraudVerifierLogicInConstructorExtractLogic(_10_DatabaseAccessorImplWithLogicInTheConstructor impl) {
		this.impl = impl;
	}

	boolean isFraud(String name) {
		Client client = impl.getClientByName(name);
		return client.hasDebt;
	}
}

class _03_DatabaseAccessorImpl {
	public Client getClientByName(String name) {
		Client client =  performLongRunningTask(name);
		System.out.println(client.name);
		doSomeAdditionalWork(client);
		return client;
	}

	private void doSomeAdditionalWork(Client client) {
		System.out.println("Additional work done");
	}

	private Client performLongRunningTask(String name) {
		throw new IllegalStateException("Can't connect to the database");
	}
}

class _10_DatabaseAccessorImplWithLogicInTheConstructor {

	_10_DatabaseAccessorImplWithLogicInTheConstructor() {
		connectToTheDatabase();
	}

	private void connectToTheDatabase() {
		throw new IllegalStateException("Can't connect to the database");
	}

	public Client getClientByName(String name) {
		Client client =  performLongRunningTask(name);
		System.out.println(client.name);
		doSomeAdditionalWork(client);
		return client;
	}

	private void doSomeAdditionalWork(Client client) {
		System.out.println("Additional work done");
	}

	private Client performLongRunningTask(String name) {
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

class _04_FakeDatabaseAccessor extends _03_DatabaseAccessorImpl {
	/**
	 * Nasz szew (seam)! Nadpisujemy problematyczną metodę bez zmiany kodu produkcyjnego
	 */
	@Override
	public Client getClientByName(String name) {
		return new Client("Fraudowski", true);
	}
}

class _14_FakeDatabaseAccessorWithInterface implements _12_DatabaseAccessor {
	@Override
	public Client getClientByName(String name) {
		return new Client("Fraudowski", true);
	}
}

interface _12_DatabaseAccessor {
	Client getClientByName(String name);
}

class DatabaseAccessorImplWithInterface implements _12_DatabaseAccessor {

	DatabaseAccessorImplWithInterface() {
		connectToTheDatabase();
	}

	@Override
	public Client getClientByName(String name) {
		Client client =  performLongRunningTask(name);
		System.out.println(client.name);
		doSomeAdditionalWork(client);
		return client;
	}

	private void connectToTheDatabase() {
		throw new IllegalStateException("Can't connect to the database");
	}

	private void doSomeAdditionalWork(Client client) {
		System.out.println("Additional work done");
	}

	private Client performLongRunningTask(String name) {
		throw new IllegalStateException("Can't connect to the database");
	}
}

class _13_FraudVerifierWithInterface {

	private final _12_DatabaseAccessor accessor;

	_13_FraudVerifierWithInterface(_12_DatabaseAccessor accessor) {
		this.accessor = accessor;
	}

	boolean isFraud(String name) {
		Client client = accessor.getClientByName(name);
		return client.hasDebt;
	}
}