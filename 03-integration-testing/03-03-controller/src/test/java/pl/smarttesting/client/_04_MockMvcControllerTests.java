package pl.smarttesting.client;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.UUID.randomUUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Klasa testowa do slajdu z testowaniem kontrolera z zamockowaną warstwą HTTP.
 *
 * Wykorzystując Spring Boota, za pomocą klasy konfigurującej MockMvcConfig,
 * uruchamiamy nasz kontekst springowy jedynie z włączoną opcją komunikacji po HTTP
 * (@WebMvcTest). Ponadto, oczekujemy, że warstaw sieciowa zostanie zamockowana
 * (adnotacja @AutoConfigureMockMvc wewnątrz adnotcji @WebMvcTest).
 *
 * W tym teście nie traktujemy kontrolera jako obiektu. Wyślemy zamockowane żądanie HTTP
 * i zweryfikujemy czy otrzymujemy rezultat, który nas interesuje.
 *
 * Rejestrujemy tylko kontroler oraz nadpisaną wersję serwisu aplikacyjnego, która
 * zwraca wartości "na sztywno".
 * Gdybyśmy w którymś z komponentów mieli połączenie z bazą danych,
 * NIE zostałoby ono zrealizowane.
 */
@WebMvcTest
@ContextConfiguration(classes = MockMvcConfig.class)
class _04_MockMvcControllerTests {

	/**
	 * Po załadowaniu klasy konfiguracyjnej, wstrzykujemy komponent MockMvc,
	 * który pozwoli nam nie nawiązywać komunikacji po HTTP.
	 *
	 * Wykorzystując klienta HTTP (tutaj MockMvc i jego fluent API), wysyłamy żądanie HTTP
	 * ze zbyt młodym Zbigniewem i oczekujemy, że dostaniemy status 401.
	 */
	@Test
	void should_reject_loan_application_when_person_too_young(@Autowired MockMvc mockMvc) throws Exception {
		mockMvc.perform(post("/fraudCheck")
				.content(tooYoungZbigniew())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}

	private String tooYoungZbigniew() {
		return "{ \"uuid\" : \"7b3e02b3-6b1a-4e75-bdad-cef5b279b074\", \"name\" : \"Zbigniew\", \"surname\" : \"Zamłodowski\", \"dateOfBirth\" : \"2020-01-01\", \"gender\" : \"MALE\", \"nationalIdentificationNumber\" : \"18210116954\" }";
	}

}

/**
 * Klasa konfiguracyjna ustawiająca prosty przypadek biznesowy z kontrolerem oraz
 * zastubowaną implementacją serwisu aplikacyjnego.
 *
 * NIE uruchamia autokonfiguracji Spring Boota.
 */
@Configuration(proxyBeanMethods = false)
class MockMvcConfig {

	/**
	 * Serwis aplikacyjny, który nie przyjmuje żadnych produkcyjnych
	 * weryfikacji. Nadpisujemy jego logikę biznesową w taki sposób,
	 * żeby zwrócone zostały dane "na sztywno". Wartość 10 została wzięta
	 * w losowy sposób, dla testów.
	 * @return serwis aplikacyjny, nie przechodzący przez kolejne warstwy aplikacji
	 */
	@Bean
	CustomerVerifier customerVerifier() {
		return new CustomerVerifier(Collections.emptySet()) {
			@Override
			CustomerVerificationResult verify(Person person) {
				if (person.getAge() < 10) {
					return CustomerVerificationResult.failed(randomUUID());
				}
				return CustomerVerificationResult.passed(randomUUID());
			}
		};
	}

	@Bean
	FraudController fraudController(CustomerVerifier customerVerifier) {
		return new FraudController(customerVerifier);
	}
}

