package pl.smarttesting.verifier.customer.verification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Schemat konfiguracyjny dla modułu weryfikacji.
 */
@Configuration(proxyBeanMethods = false)
class VerificationConfiguration {
	@Bean
	AgeVerification ageVerification() {
		return new AgeVerification();
	}

	@Bean
	IdentificationNumberVerification identificationNumberVerification() {
		return new IdentificationNumberVerification();
	}
}
