package pl.smarttesting.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	public CustomerVerifier(Set<Verification> verfications) {
		this.verifications = verfications;
	}

	/**
	 * Główna metoda biznesowa. Weryfikuje czy dana osoba jest oszustem.
	 * @param person - osoba do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	CustomerVerificationResult verify(Person person) {
		if (verifications.stream().allMatch(v -> v.passes(person))) {
			return CustomerVerificationResult.passed(person.getUuid());
		}
		return CustomerVerificationResult.failed(person.getUuid());
	}
}