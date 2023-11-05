package pl.smarttesting.verifier.model;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Schemat konfiguracyjny podpinający weryfikację klienta i komponenty dot.
 * notyfikacji (wysyłanie i słuchanie).
 */
@Configuration(proxyBeanMethods = false)
class CustomerConfiguration {

	@Bean
	_01_CustomerVerifier customerVerifier(@Autowired(required = false) Set<Verification> verifications, FraudAlertNotifier fraudAlertNotifier) {
		return new _01_CustomerVerifier(verifications != null ? verifications : Collections.emptySet(), fraudAlertNotifier);
	}

	@Bean
	_03_VerificationListener verificationListener() {
		return new _03_VerificationListener();
	}

	@Bean
	FraudAlertNotifier fraudAlertNotifier() {
		return new _04_VerificationNotifier();
	}
}