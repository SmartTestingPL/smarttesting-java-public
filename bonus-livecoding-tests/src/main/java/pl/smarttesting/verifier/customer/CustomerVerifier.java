package pl.smarttesting.verifier.customer;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.verifier.Verification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i zwraca zagregowany wynik.
 *
 * Klasa używa obiektu-wrappera otaczającego metodę statyczną realizującą operacje bazodanowe.
 * Nie polecamy robienia czegoś takiego w metodzie statycznej, ale tu pokazujemy jak to obejść i przetestować
 * jeżeli z jakiegoś powodu nie da się tego zmienić (np. metoda statyczna jest dostarczana przez kogoś innego).
 */
public class CustomerVerifier {

	private final Set<Verification> verifications;

	public CustomerVerifier(Verification... verifications) {
		this.verifications = new HashSet<>(Arrays.asList(verifications));
	}

	public CustomerVerifier(Set<Verification> verifications) {
		this.verifications = verifications;
	}

	CustomerVerificationResult verify(Customer customer) {
		if (verifications.stream()
				.allMatch(verification -> verification.passes(customer.getPerson()))) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
}


