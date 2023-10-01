package pl.smarttesting.client;

import java.net.URI;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.RequestEntity.post;

/**
 * Klasa testowa do slajdu z testowaniem kontrolera po warstwie HTTP z alokacją portu.
 *
 * Wykorzystując Spring Boota, za pomocą klasy konfigurującej PortControllerConfig,
 * startujemy serwer aplikacyjny z serwerem HTTP uruchomionym na losowym porcie.
 *
 * W tym teście nie traktujemy kontrolera jako obiektu. Wyślemy prawdziwe żądanie HTTP
 * i zweryfikujemy czy otrzymujemy rezultat, który nas interesuje.
 *
 * Ponownie uruchamiamy wszystkie warstwy naszej aplikacji: kontroler, serwis i weryfikację.
 * Gdybyśmy w którymś z komponentów mieli połączenie z bazą danych,
 * zostałoby ono zrealizowane.
 */
@SpringBootTest(
		webEnvironment = RANDOM_PORT,
		classes = PortControllerConfig.class)
class _02_FraudControllerWithPortBindingTests {

	/**
	 * Po załadowaniu klasy konfiguracyjnej, wstrzykujemy liczbę reprezentującą
	 * port na której nasłuchuje nasz uruchomiony serwer HTTP.
	 *
	 * Wykorzystując klienta HTTP (tutaj TestRestTemplate), wysyłamy żądanie HTTP
	 * ze zbyt młodym Zbigniewem i oczekujemy, że dostaniemy status 401.
	 *
	 * @param port - liczba reprezentującą port na której nasłuchuje nasz uruchomiony serwer HTTP
	 */
	@Test
	void should_reject_loan_application_when_person_too_young(@LocalServerPort int port) {
		ResponseEntity<Void> response = new TestRestTemplate()
				.exchange(post(URI.create("http://localhost:" + port + "/fraudCheck"))
						.contentType(MediaType.APPLICATION_JSON)
						.body(tooYoungZbigniew()), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	private String tooYoungZbigniew() {
		return "{ \"uuid\" : \"7b3e02b3-6b1a-4e75-bdad-cef5b279b074\", \"name\" : \"Zbigniew\", \"surname\" : \"Zamłodowski\", \"dateOfBirth\" : \"2020-01-01\", \"gender\" : \"MALE\", \"nationalIdentificationNumber\" : \"18210116954\" }";
	}

}

/**
 * Klasa konfiguracyjna ustawiająca prosty przypadek biznesowy z kontrolerem,
 * serwisem aplikacyjnym oraz jedną weryfikacją po wieku.
 *
 * Uruchamia też autokonfigurację Spring Boota (@EnableAutoConfiguration), dzięki
 * czemu serwer HTTP automatycznie się uruchomi.
 */
@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
class PortControllerConfig {

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

