package pl.smarttesting.fraudverifier.customer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.togglz.core.manager.FeatureManager;
import pl.smarttesting.fraudverifier.Verification;
import pl.smarttesting.fraudverifier.config.Features;
import pl.smarttesting.fraudverifier.customer.verification.NewTypeOfVerification;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 * Zestaw uruchamianych weryfikacji różni się w zależności od ustawień feature-togglz.
 */
public class CustomerVerifier {

	private final FeatureManager featureManager;
	private final Set<Verification> verifications;
	private final Timer verifyCustomerTimer;

	public CustomerVerifier(FeatureManager featureManager,
			MeterRegistry meterRegistry, Verification... verifications) {
		this(featureManager, meterRegistry, new HashSet<>(Arrays.asList(verifications)));
	}

	public CustomerVerifier(FeatureManager featureManager, MeterRegistry meterRegistry, Set<Verification> verifications) {
		this.verifications = verifications;
		this.featureManager = featureManager;
		verifyCustomerTimer = meterRegistry.timer("verifyCustomerTimer");
	}

	public CustomerVerificationResult verify(Customer customer) {
		return verifyCustomerTimer.record(() -> {
			Set<Verification> updatedVerifications = new HashSet<>(verifications);
			// Jeżeli feature toggle NEW_VERIFICATION jest aktywny, weryfikacja
			// typu NewTypeOfVerification zostanie uruchomiona
			if (featureManager.isActive(Features.NEW_VERIFICATION)) {
				updatedVerifications.add(new NewTypeOfVerification());
			}
			if (updatedVerifications.stream()
					.allMatch(verification -> verification
							.passes(customer.getPerson()))) {
				return CustomerVerificationResult.passed(customer.getUuid());
			}
			return CustomerVerificationResult.failed(customer.getUuid());
		});
	}
}


