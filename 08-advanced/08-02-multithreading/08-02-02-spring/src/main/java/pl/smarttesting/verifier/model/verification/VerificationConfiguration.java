package pl.smarttesting.verifier.model.verification;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration(proxyBeanMethods = false)
class VerificationConfiguration {
	@Bean
	@Order(1)
	AgeVerification ageVerification(ApplicationEventPublisher publisher) {
		return new AgeVerification(publisher);
	}

	@Bean
	@Order(2)
	IdentificationNumberVerification identificationNumberVerification(ApplicationEventPublisher publisher) {
		return new IdentificationNumberVerification(publisher);
	}

	@Bean
	@Order(3)
	NameVerification nameVerification(ApplicationEventPublisher publisher) {
		return new NameVerification(publisher);
	}
}
