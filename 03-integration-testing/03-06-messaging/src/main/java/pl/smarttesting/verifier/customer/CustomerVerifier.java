package pl.smarttesting.verifier.customer;

import java.util.Set;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas wysyłamy wiadomość do brokera,
 * z informacją o oszuście.
 */
public class CustomerVerifier {

	private final Set<Verification> verifications;

	private final VerificationRepository repository;

	private final FraudAlertNotifier fraudAlertNotifier;

	public CustomerVerifier(Set<Verification> verifications, VerificationRepository repository, FraudAlertNotifier fraudAlertNotifier) {
		this.verifications = verifications;
		this.repository = repository;
		this.fraudAlertNotifier = fraudAlertNotifier;
	}

	/**
	 * Główna metoda biznesowa. Weryfikuje czy dana osoba jest oszustem.
	 * W pozytywnym przypadku (jest oszustem) wysyła wiadomość do brokera.
	 * Zapisuje rezultat weryfikacji w bazie danych.
	 * @param customer - klient do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	CustomerVerificationResult verify(Customer customer) {
		if (!isFraud(customer)) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		CustomerVerificationResult result =
				CustomerVerificationResult.failed(customer.getUuid());
		// wyślij event jeśli znaleziono oszusta
		fraudAlertNotifier.fraudFound(
				new CustomerVerification(customer.getPerson(), result));
		storePerson(customer, result);
		return result;
	}

	private void storePerson(Customer customer, CustomerVerificationResult result) {
		repository.save(new VerifiedPerson(customer.getUuid(), customer.getPerson().getNationalIdentificationNumber(), result.getStatus()));
	}

	private boolean isFraud(Customer customer) {
		return verifications.stream().noneMatch(verification -> verification
				.passes(customer.getPerson()));
	}
}

