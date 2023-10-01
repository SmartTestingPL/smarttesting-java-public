package pl.smarttesting.client;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.UUID.randomUUID;

/**
 * Klasa testowa do slajdu z testowaniem frameworka opartego o BDD: Rest Assured
 * i jego integracji ze Spring MockMvc i kontekstem aplikacyjnym.
 */
@WebMvcTest
@ContextConfiguration(classes = RestAssuredConfig.class)
class _05_RestAssuredMockMvcWebContextTests {

	/**
	 * Opisujemy za pomocą nomenklatury BDD (Behaviour Driven Development), jak chcielibyśmy,
	 * żeby API działało.
	 *
	 * W sekcji webAppContext przekazujemy kontekst webowy, dzięki czemu kontroler sam zostanie
	 * zainicjowany ze schematu konfiguracji.
	 */
	@Test
	void should_reject_loan_application_when_person_too_young(@Autowired WebApplicationContext context) {
		given()
			.webAppContextSetup(context)
			.body(tooYoungZbigniew())
			.contentType("application/json")
		.when()
			.post("/fraudCheck")
		.then()
			.statusCode(unauthorized());
	}

	private int unauthorized() {
		return 401;
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
class RestAssuredConfig {

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

