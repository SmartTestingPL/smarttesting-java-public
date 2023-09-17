package pl.smarttesting.bik;

import java.net.URI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pl.smarttesting.bik.score.infrastructure.CustomerVerificationResult;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;

// Dotyczy lekcji 05-04
@Tag("chaos")
class ChaosEngineeringTests {

	// przykład uruchamiania testów chaosu wobec wcześniej uruchomionej aplikacji
	int port = 7654;

	@BeforeEach
	void setup() {
		enableChaosMonkey();
		invalidateCaches();
	}

	@AfterEach
	void cleanup() {
		disableChaosMonkeyWatchers();
		disableLatencyAttacks();
		disableChaosMonkey();
	}

	/**
	 * Hipoteza stanu ustalonego
	 *     GET na URL “/{pesel}”, z peselem osoby nie będącej oszustem, odpowie statusem 403, w ciągu 500 ms
	 * Metoda
	 *     Włączamy błędy spowodowane integracją z bazą danych
	 * Wycofanie
	 *     Wyłączamy błędy spowodowane integracją z bazą danych
	 */
	@Test
	void should_return_403_within_500_ms_when_checking_pesel_with_database_issues() {
		enableRepositoryWatchers();
		enableExceptionAttack();

		ResponseEntity<CustomerVerificationResult> response = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> {
			OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
			factory.setReadTimeout(500);
			return factory;
		})).exchange(RequestEntity
				.get(pesel(withNonFraudPesel())).build(), CustomerVerificationResult.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		then(response.getBody().getStatus()).isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	private void enableChaosMonkey() {
		new RestTemplate().postForObject("http://localhost:" + port + "/actuator/chaosmonkey/enable", null, String.class);
	}

	private void disableChaosMonkey() {
		new RestTemplate().postForObject("http://localhost:" + port + "/actuator/chaosmonkey/disable", null, String.class);
	}

	private static final String DISABLE_ALL = """
			{
			"controller": false,
			"restController": false,
			"service": false,
			"repository": false,
			"component": false
			}
			""";

	private static final String ENABLE_REPOSITORY_WATCHER = """
			{
			"controller": false,
			"restController": false,
			"service": false,
			"repository": true,
			"component": false
			}
			""";

	private static final String ENABLE_EXCEPTION_ATTACK = """
			{
			"level": 1,
			"latencyActive": false,
			"exceptionsActive": true,
			"killApplicationActive": false
			}
			""";

	private static final String CLEAR_ATTACKS = """
			{
			"latencyActive": false,
			"exceptionsActive": false,
			"killApplicationActive": false
			}
			""";

	private void disableChaosMonkeyWatchers() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/watchers"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(DISABLE_ALL), String.class);
	}

	private void enableRepositoryWatchers() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/watchers"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(ENABLE_REPOSITORY_WATCHER), String.class);
	}

	private void disableLatencyAttacks() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/assaults"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(CLEAR_ATTACKS), String.class);
	}

	private void enableExceptionAttack() {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/actuator/chaosmonkey/assaults"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(ENABLE_EXCEPTION_ATTACK), String.class);
	}

	private void invalidateCaches() {
		new RestTemplate().exchange(RequestEntity
				.delete(URI.create("http://localhost:" + port + "/actuator/caches")).build(), Void.class);
	}

	private String withNonFraudPesel() {
		return "89050193724";
	}

	private URI pesel(String pesel) {
		return URI.create("http://localhost:" + port + "/" + pesel);
	}
}
