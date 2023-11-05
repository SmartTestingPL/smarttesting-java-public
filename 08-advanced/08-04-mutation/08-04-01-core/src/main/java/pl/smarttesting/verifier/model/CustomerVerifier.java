package pl.smarttesting.verifier.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pl.smarttesting.customer.Customer;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
class CustomerVerifier {

	private final Set<Verification> verifications;

	public CustomerVerifier(Set<Verification> verifications) {
		this.verifications = verifications;
	}

	List<VerificationResult> verify(Customer customer) {
		return verifications.stream()
				.map(v -> v.passes(customer.getPerson()))
				.collect(Collectors.toList());
	}

}