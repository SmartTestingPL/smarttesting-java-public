package pl.smarttesting.verifier.model;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
class CustomerVerifier {

	private static final Logger log = LoggerFactory.getLogger(CustomerVerifier.class);

	private final Set<Verification> verifications;

	private final BIKVerificationService BIKVerificationService;

	private final FraudAlertNotifier fraudAlertNotifier;

	private final VerificationRepository repository;

	private final Random random = new Random();

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
	 *
	 * Najpierw łączymy się do bazy danych, żeby znaleźć daną osobę po id.
	 * Jeśli dana osoba była już zapisana w bazie, to zwracamy zapisany rezultat,
	 * w innym razie wykonujemy zapytanie do usługi zewnętrznej i również dokonujemy zapisu.
	 *
	 * @param customer - osoba do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	CustomerVerificationResult verify(Customer customer) {
		return byUserId(customer.getUuid())
				.map(p -> new CustomerVerificationResult(UUID.fromString(p.getUserId()),
						CustomerVerificationResult.Status.valueOf(p.getStatus())))
				.orElseGet(() -> calculateResult(customer));
	}

	private Optional<VerifiedPerson> byUserId(UUID id) {
		try {
			// Symulowanie różnego czasu odpowiedzi z bazy danych
			Thread.sleep(Math.abs(this.random.nextInt(1000)) + 100);
			return 	this.repository.findByUserId(id);
		} catch (Exception ex) {
			log.error("Exception occurred while trying to fetch results from DB. Will assume fraud", ex);
			return Optional.of(new VerifiedPerson() {
				@Override
				public String getUserId() {
					return id.toString();
				}

				@Override
				public String getNationalIdentificationNumber() {
					return "";
				}

				@Override
				public String getStatus() {
					return "VERIFICATION_FAILED";
				}
			});
		}
	}

	private CustomerVerificationResult calculateResult(Customer customer) {
		CustomerVerificationResult externalResult = BIKVerificationService
				.verify(customer);
		CustomerVerificationResult result = result(customer, externalResult);
		VerifiedPerson person = verifiedPerson(customer, result);
		this.repository.save(person);
		if (!result.passed()) {
			this.fraudAlertNotifier.fraudFound(new CustomerVerification(customer.getPerson(), result));
		}
		return result;
	}

	private VerifiedPerson verifiedPerson(Customer customer, CustomerVerificationResult result) {
		return new VerifiedPerson() {
			@Override
			public String getUserId() {
				return customer.getUuid().toString();
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
		return CustomerVerificationResult.passed(customer.getUuid());
	}
}

