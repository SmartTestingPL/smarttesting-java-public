package pl.smarttesting.verifier.model;

import java.util.Set;
import java.util.UUID;

import pl.smarttesting.customer.Customer;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
class CustomerVerifier {

	private final Set<Verification> verifications;

	private final BIKVerificationService BIKVerificationService;

	private final FraudAlertNotifier fraudAlertNotifier;

	private final VerificationRepository repository;

	public CustomerVerifier(BIKVerificationService BIKVerificationService,
			Set<Verification> verifications,
			FraudAlertNotifier fraudAlertNotifier, VerificationRepository repository) {
		this.BIKVerificationService = BIKVerificationService;
		this.verifications = verifications;
		this.fraudAlertNotifier = fraudAlertNotifier;
		this.repository = repository;
	}

	/**
	 * Weryfikuje czy dana osoba jest oszustem.
	 * @param customer - osoba do zweryfikowania
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
		this.repository.save(verifiedPerson(customer, result));
		if (!result.passed()) {
			this.fraudAlertNotifier.fraudFound(new CustomerVerification(customer.getPerson(), result));
		}
		return result;
	}

	private VerifiedPerson verifiedPerson(Customer customer, CustomerVerificationResult result) {
		return new VerifiedPerson() {
			@Override
			public UUID getUserId() {
				return customer.getUuid();
			}

			@Override
			public String getNationalIdentificationNumber() {
				return customer.getPerson().getNationalIdentificationNumber();
			}

			@Override
			public String getStatus() {
				return result.getStatus().toString();
			}

			@Override
			public String toString() {
				return "UserId [" + getUserId() + "] ID [" + getNationalIdentificationNumber() + "] status [" + getStatus() + "]";
			}
		};
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

