package pl.smarttesting.verifier.customer;

import java.util.Set;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i zapisuje jej wynik w bazie danych.
 * Jeśli, przy którejś okaże się, że użytkownik jest oszustem, wówczas
 * odpowiedni rezultat zostanie zwrócony.
 */
public class CustomerVerifier {

	private final Set<Verification> verifications;

	private final BIKVerificationService BIKVerificationService;

	private final VerificationRepository repository;

	public CustomerVerifier(BIKVerificationService BIKVerificationService,
			Set<Verification> verifications,
			VerificationRepository repository) {
		this.BIKVerificationService = BIKVerificationService;
		this.verifications = verifications;
		this.repository = repository;
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
				.map(this::toResult)
				.orElseGet(() -> verifyCustomer(customer));
	}

	private CustomerVerificationResult toResult(VerifiedPerson p) {
		return new CustomerVerificationResult(p.getUserId(),
				CustomerVerificationResult.Status.valueOf(p.getStatus()));
	}

	private CustomerVerificationResult verifyCustomer(Customer customer) {
		CustomerVerificationResult externalResult = BIKVerificationService
				.verify(customer);
		CustomerVerificationResult result = toResult(customer, externalResult);
		this.repository.save(verifiedPerson(customer, result));
		return result;
	}

	private VerifiedPerson verifiedPerson(Customer customer, CustomerVerificationResult result) {
		return new VerifiedPerson(customer.getUuid(), customer.getPerson().getNationalIdentificationNumber(), result.getStatus());
	}

	private CustomerVerificationResult toResult(Customer customer, CustomerVerificationResult externalResult) {
		if (verifications.stream().allMatch(verification -> verification
				.passes(customer.getPerson())) && externalResult
				.passed()) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
}

