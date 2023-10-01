package pl.smarttesting.client;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.UUID.randomUUID;

/**
 * Klasa testowa do slajdu z testowaniem frameworka opartego o BDD: Rest Assured
 * i jego integracji ze Spring MockMvc.
 */
class _05_RestAssuredMockMvcStandaloneTests {

	/**
	 * Opisujemy za pomocą nomenklatury BDD (Behaviour Driven Development), jak chcielibyśmy,
	 * żeby API działało.
	 *
	 * W sekcji standaloneSetup przekazujemy kontroler fraudController wygenerowany
	 * za pomocą testowej konfiguracji.
	 */
	@Test
	void should_reject_loan_application_when_person_too_young() {
		given()
			.standaloneSetup(new TestConfig().fraudController())
			.body(tooYoungZbigniew())
			.contentType("application/json")
		.when()
			.post("/fraudCheck")
		.then()
			.statusCode(401);
	}

	private String tooYoungZbigniew() {
		return "{ \"uuid\" : \"7b3e02b3-6b1a-4e75-bdad-cef5b279b074\", \"name\" : \"Zbigniew\", \"surname\" : \"Zamłodowski\", \"dateOfBirth\" : \"2020-01-01\", \"gender\" : \"MALE\", \"nationalIdentificationNumber\" : \"18210116954\" }";
	}

}

/**
 * Testowa klasa konfiguracyjne (nie-Springowa), rozszerzająca schemat konfiguracyjny
 * dzięki czemu wykorzystując metody z produkcyjnego schematu, jesteśmy w stanie
 * utworzyć implementację kontrolera ze sztuczną implementacją serwisu aplikacyjnego.
 *
 * W prostych przypadkach moglibyśmy po prostu wywołać bezpośrednio konstruktor
 * na kontrolerze. Np. zamiast
 *
 * .standaloneSetup(new TestConfig().fraudController())
 *
 * Moglibyśmy utworzyć sztuczną implementację CustomerVerifier - n.p. FakeCustomerVerifier
 *
 * .standaloneSetup(new FraudController(new FakeCustomerVerifier()))
 */
class TestConfig extends _05_ProductionConfiguration {
	FraudController fraudController() {
		return fraudController(fakeCustomerVerifier());
	}

	private CustomerVerifier fakeCustomerVerifier() {
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
}

/**
 * Klasa konfiguracyjna ustawiająca prosty przypadek biznesowy z kontrolerem,
 * serwisem aplikacyjnym oraz jedną weryfikacją po wieku.
 */
@Configuration(proxyBeanMethods = false)
class _05_ProductionConfiguration {

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

