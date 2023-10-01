package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.BDDAssertions.then;
import static pl.smarttesting.verifier.customer.CustomerVerificationResult.Status.VERIFICATION_PASSED;

/**
 * Klasa testowa zawiera testy aplikacji Spring Boot. Dzięki adnotacji `@EnableAutoConfiguration`
 * oraz poprzez fakt, że w zależnościach (plik `pom.xml`) jest baza danych `h2`,
 * uruchomi bazę danych i podepnie się do niej. Wskazując aktywny profil jako `inmem`
 * zostanie zaczytana konfiguracja z pliku `application-inmem.yaml`
 *
 * W tym teście widzimy, że uruchamiamy cały kontekst Springowy, razem z bazą danych
 * h2. Na wstępie uruchomiona zostaje migracja bazy danych poprzez narzędzie Flyway
 * oraz wczytanie skryptów migracyjnych. Wczytywany zostaje skrypt migracyjny produkcyjny (V1),
 * a następnie testowy `V1_1`, który dodaje użytkownika do bazy.
 */
@SpringBootTest(classes = _03_CustomerVerifierWithEmbeddedTests.Config.class)
@ActiveProfiles("inmem")
class _03_CustomerVerifierWithEmbeddedTests {

	/**
	 * Test weryfikujący, że wykorzystany zostanie ówczesnie zapisany rekord w
	 * bazie danych z rezultatem weryfikacji.
	 *
	 * W innym przypadku doszłoby do próby odpytania BIKu i rzucony zostałby
	 * wyjątek.
	 *
	 * @param repository - wstrzyknięte repozytorium z dostępem do bazy danych
	 * @param verifier - serwis aplikacyjny do przetestowania
	 */
	@Test
	void should_successfully_verify_a_customer_when_previously_verified(@Autowired VerificationRepository repository, @Autowired CustomerVerifier verifier) {
		Customer zbigniew = zbigniew();
		then(repository.findByUserId(zbigniew.getUuid())).isPresent();

		CustomerVerificationResult result = verifier.verify(zbigniew);

		then(result.getUserId()).isEqualTo(zbigniew().getUuid());
		then(result.getStatus()).isEqualTo(VERIFICATION_PASSED);
	}

	private Customer zbigniew() {
		return new Customer(UUID.fromString("89c878e3-38f7-4831-af6c-c3b4a0669022"), youngZbigniew());
	}

	private Person youngZbigniew() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "18210116954");
	}

	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	static class Config extends CustomerConfiguration {
		@Override
		BIKVerificationService bikVerificationService(String url) {
			return new ExceptionThrowingBikVerifier();
		}
	}
}
