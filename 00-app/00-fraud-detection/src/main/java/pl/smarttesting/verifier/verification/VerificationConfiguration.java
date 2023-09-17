package pl.smarttesting.verifier.verification;

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
	GenderVerification genderVerification() {
		return new GenderVerification();
	}

	@Bean
	IdentificationNumberVerification identificationNumberVerification() {
		return new IdentificationNumberVerification();
	}

	@Bean
	NameVerification nameVerification() {
		return new NameVerification();
	}

	@Bean
	SurnameVerification surnameVerification() {
		return new SurnameVerification();
	}

}
