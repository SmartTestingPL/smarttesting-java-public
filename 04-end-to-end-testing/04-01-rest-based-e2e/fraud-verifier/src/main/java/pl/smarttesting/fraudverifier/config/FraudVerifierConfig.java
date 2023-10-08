package pl.smarttesting.fraudverifier.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	CustomerVerifier customerVerifier(Set<Verification> verifications) {
		return new CustomerVerifier(verifications);
	}
}
