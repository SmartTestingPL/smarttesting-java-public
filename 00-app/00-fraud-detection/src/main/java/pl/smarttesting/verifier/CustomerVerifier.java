package pl.smarttesting.verifier;

import java.util.Collections;
import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i zapisuje jej wynik w bazie danych.
 * W przypadku wcześniejszego zapisu wyciąga zawartość z bazy danych.
 * Jeśli, przy którejś okaże się, że użytkownik jest oszustem, wówczas
 * odpowiedni rezultat zostanie zwrócony.
 */
@Service
public class CustomerVerifier {

	private static final Logger log = LoggerFactory.getLogger(CustomerVerifier.class);

	private final Set<Verification> verifications;

	private final BIKVerificationService BIKVerificationService;

	private final VerificationRepository repository;

	private final FraudAlertNotifier fraudAlertNotifier;

	private final MeterRegistry meterRegistry;

	public CustomerVerifier(BIKVerificationService BIKVerificationService,
		ObjectProvider<Set<Verification>> verifications,
		VerificationRepository repository, FraudAlertNotifier fraudAlertNotifier, MeterRegistry meterRegistry) {
		this.BIKVerificationService = BIKVerificationService;
		this.verifications = verifications.getIfAvailable(Collections::emptySet);
		this.repository = repository;
		this.fraudAlertNotifier = fraudAlertNotifier;
		this.meterRegistry = meterRegistry;
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
		Timer.Sample customerVerificationSample = Timer.start(meterRegistry);
		log.info("Customer with id [{}] not found in the database. Will calculate the new result", customer.getUuid());
		CustomerVerificationResult externalResult = BIKVerificationService
				.verify(customer);
		log.info("The result from BIK was [{}]", externalResult);
		CustomerVerificationResult result = result(customer, externalResult);
		log.info("The result from other checks was [{}]", result);
		this.repository.save(new VerifiedPerson(customer.getUuid(), customer.getPerson().getNationalIdentificationNumber(), result.getStatus()));
		if (!result.passed()) {
			this.fraudAlertNotifier.fraudFound(new CustomerVerification(customer.getPerson(), result));
		}
		customerVerificationSample.stop(meterRegistry.timer("customer.verification"));
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

