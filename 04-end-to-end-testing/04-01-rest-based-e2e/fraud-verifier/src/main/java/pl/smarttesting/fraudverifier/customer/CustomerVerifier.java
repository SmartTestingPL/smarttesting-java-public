package pl.smarttesting.fraudverifier.customer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pl.smarttesting.fraudverifier.Verification;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
public class CustomerVerifier {

	private final Set<Verification> verifications;

	public CustomerVerifier(Verification... verifications) {
		this.verifications = new HashSet<>(Arrays.asList(verifications));
	}

	public CustomerVerifier(Set<Verification> verifications) {
		this.verifications = verifications;
	}

	public CustomerVerificationResult verify(Customer customer) {
		if (verifications.stream()
				.allMatch(verification -> verification.passes(customer.getPerson()))) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
}


