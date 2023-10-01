package pl.smarttesting.verifier.customer;

import pl.smarttesting.customer.Customer;

/**
 * Testowa implementacja komunikacji z BIK. Zwraca pozytywną weryfikację
 * (dana osoba nie jest oszustem).
 */
class AlwaysPassingBikVerifier extends BIKVerificationService {

	public AlwaysPassingBikVerifier() {
		super("");
	}

	@Override
	public CustomerVerificationResult verify(Customer customer) {
		return CustomerVerificationResult.passed(customer.getUuid());
	}
}
