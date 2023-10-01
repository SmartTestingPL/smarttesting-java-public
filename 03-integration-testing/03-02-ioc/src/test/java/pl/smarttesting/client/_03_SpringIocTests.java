package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class _03_SpringIocTests {

	/**
	 * Kod przedstawiony na slajdzie po zdefiniowaniu klasy konfiguracyjnej.
	 * Ten test uruchamia kontekst aplikacyjny Springa na podstawie zdefiniowanego
	 * schematu. Oddzielamy w ten sposób konstrukcję
	 *
	 * ApplicationContext context = new AnnotationConfigApplicationContext(_02_Config.class);
	 * CustomerVerifier customerVerifier = context.getBean(CustomerVerifier.class);
	 *
	 * od użycia
	 *
	 * CustomerVerificationResult result = customerVerifier.verify(stefan());
	 */
	@Test
	void springContextUsage() {
		// Uruchomienie kontekstu na podstawie konfiguracji
		ApplicationContext context = new AnnotationConfigApplicationContext(_02_Config.class);

		// Wyciągnięcie obiektu z kontekstu
		CustomerVerifier customerVerifier = context.getBean(CustomerVerifier.class);

		// Wywołanie logiki biznesowej
		CustomerVerificationResult result = customerVerifier.verify(stefan());

		// Asercja
		BDDAssertions.then(result.getStatus()).isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	Person stefan() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "");
	}

}


