package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.BDDAssertions.then;
import static pl.smarttesting.client.CustomerVerificationResult.Status.VERIFICATION_FAILED;

/**
 * Narzędzie do IOC / DI, mogą posiadać integrację z narzędziami do testów
 * Tutaj widzimy wykorzystanie integracji Springa z Frameworkiem JUnit5.
 *
 * Zaczytanie konfiguracji następuje poprzez `@SpringJUnitConfig`, do której
 * przekazujemy schemat konfiguracyjny.
 *
 * Za pomocą adnotacji `@Autowired` wyciągamy z kontekstu dany obiekt i wstrzykujemy
 * sobie do testu.
 */
// Uruchomienie kontekstu na podstawie konfiguracji
@SpringJUnitConfig(_02_Config.class)
class _05_CustomerVerificationTests {

	// Wyciągnięcie obiektu z kontekstu
	@Test
	void should_pass_verification_when_non_fraud_gets_verified(@Autowired CustomerVerifier customerVerifier) {
		// Wywołanie logiki biznesowej
		CustomerVerificationResult result = customerVerifier.verify(tooYoungStefan());

		then(result.getStatus()).isEqualTo(VERIFICATION_FAILED);
	}

	Person tooYoungStefan() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "");
	}

}
