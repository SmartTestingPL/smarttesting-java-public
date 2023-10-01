package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Klasa testowa do slajdu z testowaniem kontrolera jako obiektu.
 *
 * Jeśli zainicjujemy kontroler jako obiekt oraz jego zależności to z punktu widzenia kontrolera
 * mamy nic innego jak test jednostkowy. W taki sposób testujemy bez warstwy HTTP
 * logikę naszych komponentów. Zakładając, że przetestowaliśmy jednostkowo
 * `customerVerifier`, taki test nam nic nie daje.
 *
 * Zatem skoro naszym celem jest zweryfikowanie czy nasz kontroler komunikuje się po warstwie HTTP to kompletnie nam się to nie udało.
 *
 * Czy jest to zły test? Nie, ale trzeba włączyć w to testowanie warstwy HTTP.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ControllerTestsConfig.class)
class _01_ControllerTests {

	/**
	 * Po załadowaniu klasy konfiguracyjnej ControllerTestsConfig, która definiuje nam
	 * kontekst naszej aplikacji, wstrzykujemy kontroler i przechodzimy przez wszystkie
	 * warstwy controller -> verifier -> verification.
	 *
	 * @param fraudController - kontroler jako obiekt wyciągnięty z kontekstu
	 */
	@Test
	void should_reject_loan_application_when_person_too_young(@Autowired FraudController fraudController) {
		ResponseEntity<Void> entity = fraudController.fraudCheck(tooYoungZbigniew());

		BDDAssertions.then(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	Person tooYoungZbigniew() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "");
	}

}

/**
 * Klasa konfiguracyjna ustawiająca prosty przypadek biznesowy z kontrolerem,
 * serwisem aplikacyjnym oraz jedną weryfikacją po wieku.
 */
@Configuration(proxyBeanMethods = false)
class ControllerTestsConfig {

	@Bean
	AgeVerification ageVerification() {
		return new AgeVerification();
	}

	@Bean
	CustomerVerifier customerVerifier(Set<Verification> verfications) {
		return new CustomerVerifier(verfications);
	}

	@Bean
	FraudController fraudController(CustomerVerifier customerVerifier) {
		return new FraudController(customerVerifier);
	}
}

