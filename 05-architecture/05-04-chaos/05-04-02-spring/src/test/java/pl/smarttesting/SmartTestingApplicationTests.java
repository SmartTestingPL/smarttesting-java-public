package pl.smarttesting;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Klasa testowa wykorzystująca narzędzie Chaos Monkey do zmiany zachowania naszej aplikacji
 * w czasie rzeczywistym w celu weryfikacji hipotez stanu ustalonego w ramach eksperymentów
 * inżynierii chaosu.
 *
 * Test uruchomi się jedynie, gdy zmienna systemowa chaos jest włączona. Możemy mieć te testy
 * w ramach naszej suity testów, ale niekoniecznie zawsze będziemy chcieli automatycznie je uruchamiać.
 */
@EnabledIfSystemProperty(named = "chaos", matches = "true")
class SmartTestingApplicationTests {

	/**
	 * Testy uruchamiamy wobec działającej aplikacji na środowisku (pre)produkcyjnym.
	 * Musimy podać na jakim porcie znajduje się uruchomiona aplikacja.
	 */
	int port = 4321;

	/**
	 * Przed każdym testem włączamy obsługę chaos monkey.
	 */
	@BeforeEach
	void setup() {
		enableChaosMonkey();
	}

	/**
	 * Po każdym teście wyłączamy zmianę zachowań, wyłączamy ataki i obsługę chaos monkey.
	 */
	@AfterEach
	void cleanup() {
		disableChaosMonkeyWatchers();
		disableLatencyAttacks();
		disableChaosMonkey();
	}

	/**
	 * Hipoteza stanu ustalonego
	 *     POST na URL “/fraudCheck”,  reprezentujący oszusta, odpowie statusem 401, w ciągu 500 ms
	 * Metoda
	 *     Włączamy opóźnienie mające miejsce w kontrolerze
	 * Wycofanie
	 *     Wyłączamy opóźnienie mające miejsce w kontrolerze
	 */
	@Test
	void should_return_401_within_500_ms_when_calling_fraud_check_with_introduced_latency() {
		enableControllerWatchers();
		enableLatencyAttack();

		ResponseEntity<Void> response = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> {
			OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
			factory.setReadTimeout(500);
			return factory;
		})).exchange(RequestEntity
				.post(fraudCheck())
				.contentType(MediaType.APPLICATION_JSON)
				.body(FRAUD), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Hipoteza stanu ustalonego
	 *     POST na URL “/fraudCheck”,  reprezentujący oszusta, odpowie statusem 401, w ciągu 500 ms
	 * Metoda
	 *     Włączamy błędy spowodowane integracją z bazą danych
	 * Wycofanie
	 *     Wyłączamy błędy spowodowane integracją z bazą danych
	 */
	@Test
	void should_return_401_within_500_ms_when_calling_fraud_check_with_database_issues() {
		enableRepositoryWatchers();
		enableExceptionAttack();

		ResponseEntity<Void> response = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> {
			OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
			factory.setReadTimeout(500);
			return factory;
		})).exchange(RequestEntity
				.post(fraudCheck())
				.contentType(MediaType.APPLICATION_JSON)
				.body(FRAUD), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Po API RESTowym włączamy narzędzie do inżynierii chaosu.
	 */
	private void enableChaosMonkey() {
		new RestTemplate().postForObject("http://localhost:" + port + "/actuator/chaosmonkey/enable", null, String.class);
	}

	/**
	 * Po API RESTowym wyłączamy narzędzie do inżynierii chaosu.
	 */
	private void disableChaosMonkey() {
		new RestTemplate().postForObject("http://localhost:" + port + "/actuator/chaosmonkey/disable", null, String.class);
	}

	private static final String FRAUD = "{\n"
			+ "  \"uuid\" : \"cc8aa8ff-40ff-426f-bc71-5bb7ea644108\",\n"
			+ "  \"person\" : {\n"
			+ "    \"name\" : \"Fraudeusz\",\n"
			+ "    \"surname\" : \"Fraudowski\",\n"
			+ "    \"dateOfBirth\" : \"01-01-1980\",\n"
			+ "    \"gender\" : \"MALE\",\n"
			+ "    \"nationalIdentificationNumber\" : \"2345678901\"\n"
			+ "  }\n"
			+ "}";

	private static final String DISABLE_ALL = "{\n"
			+ "\"controller\": false,\n"
			+ "\"restController\": false,\n"
			+ "\"service\": false,\n"
			+ "\"repository\": false,\n"
			+ "\"component\": false\n"
			+ "}";

	private static final String ENABLE_CONTROLLER_WATCHER = "{\n"
			+ "\"controller\": false,\n"
			+ "\"restController\": true,\n"
			+ "\"service\": false,\n"
			+ "\"repository\": false,\n"
			+ "\"component\": false\n"
			+ "}";

	private static final String ENABLE_REPOSITORY_WATCHER = "{\n"
			+ "\"controller\": false,\n"
			+ "\"restController\": false,\n"
			+ "\"service\": false,\n"
			+ "\"repository\": true,\n"
			+ "\"component\": false\n"
			+ "}";

	private static final String ENABLE_LATENCY_ATTACK = "{\n"
			+ "\"level\": 1,\n"
			+ "\"latencyRangeStart\": 1000,\n"
			+ "\"latencyRangeEnd\": 3000,\n"
			+ "\"latencyActive\": true,\n"
			+ "\"exceptionsActive\": false,\n"
			+ "\"killApplicationActive\": false\n"
			+ "}";

	private static final String ENABLE_EXCEPTION_ATTACK = "{\n"
			+ "\"level\": 1,\n"
			+ "\"latencyActive\": false,\n"
			+ "\"exceptionsActive\": true,\n"
			+ "\"killApplicationActive\": false\n"
			+ "}";

	private static final String CLEAR_ATTACKS = "{\n"
			+ "\"latencyActive\": false,\n"
			+ "\"exceptionsActive\": false,\n"
			+ "\"killApplicationActive\": false\n"
			+ "}";

	/**
	 * Po API RESTowym włączamy funkcje przechwytywania.
	 */
	private void disableChaosMonkeyWatchers() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/watchers"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(DISABLE_ALL), String.class);
	}

	/**
	 * Po API RESTowym włączamy przechwytywanie kontrolerów.
	 */
	private void enableControllerWatchers() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/watchers"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(ENABLE_CONTROLLER_WATCHER), String.class);
	}

	/**
	 * Po API RESTowym włączamy przechwytywanie repozytoriów.
	 */
	private void enableRepositoryWatchers() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/watchers"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(ENABLE_REPOSITORY_WATCHER), String.class);
	}

	/**
	 * Po API RESTowym wyłączamy ataki związane z opóźnieniem.
	 */
	private void disableLatencyAttacks() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/assaults"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(CLEAR_ATTACKS), String.class);
	}

	/**
	 * Po API RESTowym włączamy ataki związane z opóźnieniem.
	 */
	private void enableLatencyAttack() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/assaults"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(ENABLE_LATENCY_ATTACK), String.class);
	}

	/**
	 * Po API RESTowym włączamy ataki związane z rzucaniem wyjątków.
	 */
	private void enableExceptionAttack() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/assaults"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(ENABLE_EXCEPTION_ATTACK), String.class);
	}

	private URI fraudCheck() {
		return URI.create("http://localhost:" + port + "/fraudCheck");
	}
}