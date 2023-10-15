package pl.smarttesting.verifier.model;

import java.util.Optional;
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
	 * Mamy tu problem w postaci braku obsługi jakichkolwiek wyjątków. Co jeśli baza danych
	 * rzuci wyjątkiem? Jak powinniśmy to obsłużyć biznesowo?
	 *
	 * Po pierwszym uruchomieniu eksperymentu z dziedziny inżynierii chaosu, który wywali test
	 * dot. bazy danych, zakomentuj linijkę
	 *
	 * {@code return this.repository.findByUserId(customer.getUuid())}
	 *
	 * i odkomentuj tę
	 *
	 * {@code return byUserId(customer.getUuid())}
	 *
	 * wówczas jeden z dwóch testów przejdzie, ponieważ obsługujemy poprawnie błędy bazodanowe.
	 *
	 * @param customer - osoba do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	CustomerVerificationResult verify(Customer customer) {
		return this.repository.findByUserId(customer.getUuid())
//		return byUserId(customer.getUuid())
				.map(p -> new CustomerVerificationResult(UUID.fromString(p.getUserId()),
						CustomerVerificationResult.Status.valueOf(p.getStatus())))
				.orElseGet(() -> calculateResult(customer));
	}

	/**
	 * Poprawiona wersja odpytania o wyniki z bazy danych. Jeśli wyjątek został rzucony,
	 * zalogujemy go, ale z punktu widzenia biznesowego możemy spokojnie założyć, że
	 * mamy do czynienia z potencjalnym oszustem.
	 *
	 * @param id - id osoby, której szukaliśmy
	 * @return osoba z bazy danych lub potencjalny oszust
	 */
	private Optional<VerifiedPerson> byUserId(UUID id) {
		try {
			return this.repository.findByUserId(id);
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
		this.repository.save(verifiedPerson(customer, result));
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
		if (verifications.stream().allMatch(verification -> verification
				.passes(customer.getPerson())) && externalResult
				.passed()) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
}

