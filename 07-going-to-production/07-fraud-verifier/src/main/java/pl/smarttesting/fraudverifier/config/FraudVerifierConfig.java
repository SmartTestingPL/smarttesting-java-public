package pl.smarttesting.fraudverifier.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import org.togglz.core.manager.FeatureManager;
import pl.smarttesting.fraudverifier.Verification;
import pl.smarttesting.fraudverifier.customer.CustomerVerifier;
import pl.smarttesting.fraudverifier.customer.verification.AgeVerification;
import pl.smarttesting.fraudverifier.customer.verification.IdentificationNumberVerification;
import pl.smarttesting.fraudverifier.customer.verification.NameVerification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracja weryfikacji klientów.
 */
@Configuration(proxyBeanMethods = false)
public class FraudVerifierConfig {

	@Bean
	Set<Verification> verifications() {
		return new HashSet<>(Arrays
				.asList(new AgeVerification(), new IdentificationNumberVerification(),
						new NameVerification()));
	}

	@Bean
	CustomerVerifier customerVerifier(Set<Verification> verifications, MeterRegistry meterRegistry,
			FeatureManager featureManager) {
		return new CustomerVerifier(featureManager, meterRegistry, verifications);
	}
}
