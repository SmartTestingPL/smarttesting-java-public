package pl.smarttesting.e2e;

import java.net.URI;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;

// Dotyczy lekcji 05-04
@Tag("chaos")
@Testcontainers
class ChaosEngineeringTests implements ContainerRunning {

	// przykład uruchomienia testów chaosu wobec automatycznie uruchamianego środowiska
	@Container
	static DockerComposeContainer container = ContainerRunning.infrastructure();

	@Container
	GenericContainer bikService = latestBikService();

	volatile GenericContainer fraudService = null;

	/**
	 * Hipoteza stanu ustalonego
	 *     POST na URL “/{fraudCheck}”, z peselem osoby nie będącej oszustem, odpowie statusem 401, w ciągu 2000 ms
	 * Metoda
	 *     Włączamy błędy spowodowane integracją z komponentami typu @Service
	 * Wycofanie
	 *     Wyłączamy błędy spowodowane integracją z komponentami typu @Service
	 */
	@Disabled("Wywali się bo nie mamy obsługi błędów na poziomie kontrollerów (wyjątek poleci z serviceu)")
	@Test
	void should_return_401_within_2000_ms_when_checking_non_fraud_but_services_are_down() {
		try {
			startFraudService(bikService);
			setupChaos(fraudService);
			enableServiceWatchers(fraudService);
			enableExceptionAttack(fraudService);
			invalidateCaches(bikService, fraudService);

			ResponseEntity<Void> response = new TestRestTemplate(new RestTemplateBuilder().requestFactory(() -> {
				OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory();
				factory.setReadTimeout(2000);
				return factory;
			})).exchange(RequestEntity
					.post(fraudCheck(fraudService))
					.contentType(APPLICATION_JSON)
					.body(NON_FRAUD), Void.class);

			then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		} finally {
			cleanup(fraudService);
		}
	}

	void setupChaos(GenericContainer fraudService) {
		enableChaosMonkey(fraudService);
	}

	void cleanup(GenericContainer fraudService) {
		if (fraudService != null) {
			disableChaosMonkeyWatchers(fraudService);
			fraudService.close();
		}
	}

	@Override
	public GenericContainer fraudService(GenericContainer bikService) {
		if (this.fraudService == null) {
			this.fraudService =  ContainerRunning.super.fraudService(bikService)
					.withEnv("SPRING_PROFILES_ACTIVE", "chaos-monkey");
		}
		return this.fraudService;
	}

	private void enableChaosMonkey(GenericContainer fraudService) {
		new RestTemplate().postForObject("http://localhost:" + fraudService.getFirstMappedPort() + "/actuator/chaosmonkey/enable", null, String.class);
	}

	private static final String NON_FRAUD = """
			{
			   "uuid" : "5cd495e7-9a66-4c4b-bba2-8d15cc8d9e68",
			   "person" : {
			     "name" : "Stefania",
			     "surname" : "Stefanowska",
			     "dateOfBirth" : "01-05-1989",
			     "gender" : "FEMALE",
			     "nationalIdentificationNumber" : "89050193724"
			   }
			 }
			 """;

	private static final String DISABLE_ALL = """
			{
			"controller": false,
			"restController": false,
			"service": false,
			"repository": false,
			"component": false
			}
			""";

	private static final String ENABLE_SERVICE_WATCHER = """
			{
			"controller": false,
			"restController": false,
			"service": true,
			"repository": false,
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

	private void disableChaosMonkeyWatchers(GenericContainer fraudService) {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + fraudService.getFirstMappedPort() + "/actuator/chaosmonkey/watchers"))
				.contentType(APPLICATION_JSON)
				.body(DISABLE_ALL), String.class);
	}

	private void enableServiceWatchers(GenericContainer fraudService) {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + fraudService.getFirstMappedPort() + "/actuator/chaosmonkey/watchers"))
				.contentType(APPLICATION_JSON)
				.body(ENABLE_SERVICE_WATCHER), String.class);
	}

	private void enableExceptionAttack(GenericContainer fraudService) {
		new RestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + fraudService.getFirstMappedPort() + "/actuator/chaosmonkey/assaults"))
				.contentType(APPLICATION_JSON)
				.body(ENABLE_EXCEPTION_ATTACK), String.class);
	}

	private void invalidateCaches(GenericContainer... services) {
		for (GenericContainer service : services) {
			new RestTemplate().exchange(RequestEntity
					.delete(URI.create("http://localhost:" + service.getFirstMappedPort() + "/actuator/caches")).build(), Void.class);
		}
	}

	private URI fraudCheck(GenericContainer fraudService) {
		return URI.create("http://localhost:" + fraudService.getFirstMappedPort() + "/fraudCheck");
	}
}
