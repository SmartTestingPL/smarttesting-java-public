package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
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
 * Klasa testowa zawiera testy aplikacji Spring Boot.
 *
 * Poprzez użycie Testcontainers uruchomi w kontenerze bazę danych PostgreSQL
 *
 * Dzięki adnotacji `@EnableAutoConfiguration`
 * oraz poprzez fakt, że w zależnościach (plik `pom.xml`) jest baza danych `postgresql`,
 * podepnie się do niej. Wskazując aktywny profil jako `container`
 * zostanie zaczytana konfiguracja z pliku `application-container.yaml`
 *
 * W tym teście widzimy, że uruchamiamy kontener z bazą danych,
 * cały kontekst Springowy. Na wstępie uruchomiona zostaje migracja
 * bazy danych poprzez narzędzie Flyway
 * oraz wczytanie skryptów migracyjnych. Wczytywany zostaje skrypt migracyjny produkcyjny (V1),
 * a następnie testowy `V1_1`, który dodaje użytkownika do bazy.
 */
@SpringBootTest(classes = _04_CustomerVerifierWithContainersTests.Config.class)
@ActiveProfiles("container")
@Testcontainers
class _04_CustomerVerifierWithContainersTests {

	private static final String POSTGRES_IMAGE = "postgres:14.1";
	/**
	 * Uruchomienie kontenera z bazą danych na losowym porcie przed uruchomieniem testów.
	 */
	@Container
	private static final PostgreSQLContainer DB_CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE);

	/**
	 * W bloku statycznym ustawiamy zmienną DB_PORT wartością portu, na którym uruchomiona jest
	 * baza danych. Tak wykorzystany losowy port będzie zaczytany w konfiguracji
	 * application-container.yaml.
	 */
	static {
		DB_CONTAINER.start();
		System.setProperty("DB_PORT", String.valueOf(DB_CONTAINER.getFirstMappedPort()));
	}

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
