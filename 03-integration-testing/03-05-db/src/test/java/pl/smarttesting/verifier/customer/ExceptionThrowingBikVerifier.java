package pl.smarttesting.verifier.customer;

import pl.smarttesting.customer.Customer;

/**
 * Testowa implementacja komunikacji z BIK. Rzuca wyjątkiem jeśli zostanie wywołana.
 * W ten sposób upewniamy się, że test się wysypie jeśli spróbujemy zawołać BIK.
 */
class ExceptionThrowingBikVerifier extends BIKVerificationService {

	public ExceptionThrowingBikVerifier() {
		super("");
	}

	@Override
	public CustomerVerificationResult verify(Customer customer) {
		throw new IllegalStateException("Shouldn't call bik verification");
	}
}
