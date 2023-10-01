package pl.smarttesting.verifier;

import java.util.Set;

import pl.smarttesting.customer.Customer;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i zapisuje jej wynik w bazie danych.
 * W przypadku wcześniejszego zapisu wyciąga zawartość z bazy danych.
 * Jeśli, przy którejś okaże się, że użytkownik jest oszustem, wówczas
 * odpowiedni rezultat zostanie zwrócony.
 *
 */
public class CustomerVerifier {

	private final Set<Verification> verifications;

	private final BIKVerificationService BIKVerificationService;

	private final VerificationRepository repository;

	private final FraudAlertNotifier fraudAlertNotifier;

	public CustomerVerifier(BIKVerificationService BIKVerificationService,
			Set<Verification> verifications,
			VerificationRepository repository, FraudAlertNotifier fraudAlertNotifier) {
		this.BIKVerificationService = BIKVerificationService;
		this.verifications = verifications;
		this.repository = repository;
		this.fraudAlertNotifier = fraudAlertNotifier;
	}

	/**
	 * Główna metoda biznesowa. Sprawdza, czy już nie doszło do weryfikacji klienta i jeśli
	 * rezultat zostanie odnaleziony w bazie danych to go zwraca. W innym przypadku zapisuje
	 * wynik weryfikacji w bazie danych. Weryfikacja wówczas zachodzi poprzez odpytanie
	 * BIKu o stan naszego klienta.
	 *
	 * @param customer - klient do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	CustomerVerificationResult verify(Customer customer) {
		return this.repository.findByUserId(customer.getUuid())
				.map(p -> new CustomerVerificationResult(p.getUserId(),
						CustomerVerificationResult.Status.valueOf(p.getStatus())))
				.orElseGet(() -> calculateResult(customer));
	}

	private CustomerVerificationResult calculateResult(Customer customer) {
		CustomerVerificationResult externalResult = BIKVerificationService
				.verify(customer);
		CustomerVerificationResult result = result(customer, externalResult);
		this.repository.save(new VerifiedPerson(customer.getUuid(), customer.getPerson().getNationalIdentificationNumber(), result.getStatus()));
		if (!result.passed()) {
			this.fraudAlertNotifier.fraudFound(new CustomerVerification(customer.getPerson(), result));
		}
		return result;
	}

	private CustomerVerificationResult result(Customer customer, CustomerVerificationResult externalResult) {
		if (verifications.stream().allMatch(verification -> verification
				.passes(customer.getPerson())) && externalResult
				.passed()) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
}

