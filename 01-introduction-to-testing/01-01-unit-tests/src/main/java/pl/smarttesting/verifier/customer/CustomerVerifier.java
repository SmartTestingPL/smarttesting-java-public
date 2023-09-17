package pl.smarttesting.verifier.customer;

import java.util.Set;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.verifier.Verification;

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

	private final BIKVerificationService BIKVerificationService;

	private final VeryBadVerificationServiceWrapper serviceWrapper;

	public CustomerVerifier(BIKVerificationService BIKVerificationService,
			Set<Verification> verifications, VeryBadVerificationServiceWrapper serviceWrapper) {
		this.BIKVerificationService = BIKVerificationService;
		this.verifications = verifications;
		this.serviceWrapper = serviceWrapper;
	}

	CustomerVerificationResult verify(Customer customer) {
		CustomerVerificationResult externalResult = BIKVerificationService
				.verify(customer);
		if (verifications.stream().allMatch(verification -> verification
				.passes(customer.getPerson())) && externalResult
				.passed() && serviceWrapper.verify()) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}
}
