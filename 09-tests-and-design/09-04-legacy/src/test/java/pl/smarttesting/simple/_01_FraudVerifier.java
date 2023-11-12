package pl.smarttesting.simple;

class _01_FraudVerifier {

	private final DatabaseAccessorImpl impl;

	_01_FraudVerifier(DatabaseAccessorImpl impl) {
		this.impl = impl;
	}

	boolean isFraud(String name) {
		Client client = impl.getClientByName(name);
		return client.hasDebt;
	}
}

class DatabaseAccessorImpl {
	Client getClientByName(String name) {
		return new Client();
	}
}

class Client {
	boolean hasDebt;
}