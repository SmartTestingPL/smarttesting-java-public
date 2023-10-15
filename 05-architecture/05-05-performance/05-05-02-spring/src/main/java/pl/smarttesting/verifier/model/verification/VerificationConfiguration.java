package pl.smarttesting.verifier.model.verification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Schemat konfiguracyjny dla elementów domeny głównej.
 * Dopiero tutaj wpinamy te obiekty w kontener Springa.
 *
 * Dzięki temu, że stworzyliśmy tę klasę w tym samym pakiecie jak zdefiniowane są one
 * w domenie głównej, te klasy są w ogóle dla nas widoczne.
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
