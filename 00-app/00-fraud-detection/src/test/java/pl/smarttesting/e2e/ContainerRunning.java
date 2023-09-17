package pl.smarttesting.e2e;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

/**
 * Metody pomocnicze do obsługi uruchamiania kontenerów.
 */
interface ContainerRunning {
	
	Logger log = LoggerFactory.getLogger(ContainerRunning.class);

	/**
	 * URL z plikiem docker-compose dot. naszych testów e2e.
	 */
	URL composeForE2e = ContainerRunning.class.getResource("/docker/docker-compose-e2e.yml");
	
	/**
	 * Narzędzia pomocnicze do uzyskania IP hosta. 
	 */
	InetUtils inetUtils = new InetUtils(new InetUtilsProperties());
	
	/**
	 * IP hosta. Przekazając IP hosta unikamy problemów z mapowaniem sieci i portów między kontenerami.
	 */
	String EXTERNAL_IP = inetUtils.findFirstNonLoopbackAddress().getHostAddress();

	/**
	 * @return kontener z najnowszą wersją bik-service 
	 */
	default GenericContainer latestBikService() {
		return new GenericContainer(DockerImageName.parse("smarttesting/bik-service:latest"))
			.withExposedPorts(7654)
			.withEnv("SERVER_PORT", "7654")
			.withEnv("BIK_SCORE_THRESHOLD", "10")
			.withEnv("DB_HOST", EXTERNAL_IP)
			.withEnv("RABBIT_HOST", EXTERNAL_IP)
			.withEnv("MONGO_HOST", EXTERNAL_IP)
			.withEnv("ZIPKIN_HOST", EXTERNAL_IP)
			.withEnv("MONTHLY_INCOME_URL", "http://" + EXTERNAL_IP + ":1234")
			.withEnv("PERSONAL_URL", "http://" + EXTERNAL_IP + ":2345")
			.withEnv("MONTHLY_COST_URL", "http://" + EXTERNAL_IP + ":3456")
			.withEnv("SOCIAL_URL", "http://" + EXTERNAL_IP + ":4567")
			.waitingFor(Wait.forHttp("/actuator/health").withStartupTimeout(Duration.ofSeconds(60)))
			.withLogConsumer(new Slf4jLogConsumer(log));
	}
	

	/**
	 * @return kontenery z uruchomioną infrastrukturą 
	 */
	static DockerComposeContainer infrastructure() {
		return new DockerComposeContainer(compose())
				 .withLocalCompose(true)
				.withExposedService("zipkin_1", 9411,  Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
				.withExposedService("postgres_1", 5432,  Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
				.withExposedService("rabbitmq_1", 5672,  Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
				.withExposedService("mongo_1", 27017,  Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
				.withExposedService("income-wiremock_1", 1234, Wait.forLogMessage(".*enable-browser-proxying.*", 1))
				.withExposedService("personal-wiremock_1", 2345, Wait.forLogMessage(".*enable-browser-proxying.*", 1))
				.withExposedService("monthly-cost-wiremock_1", 3456, Wait.forLogMessage(".*enable-browser-proxying.*", 1))
				.withExposedService("social-wiremock_1", 4567, Wait.forLogMessage(".*enable-browser-proxying.*", 1));
	}
	

	/**
	 * @param bikService kontener bik-service
	 * @return uruchomiony kontener fraud-service
	 */
	default GenericContainer startFraudService(GenericContainer bikService) {
		GenericContainer container = fraudService(bikService);
		container.start();
		return container;
	}

	default GenericContainer fraudService(GenericContainer bikService) {
		return new GenericContainer(DockerImageName.parse("smarttesting/fraud-detection:latest"))
				.withExposedPorts(8765)
				.withEnv("SERVER_PORT", "8765")
				.withEnv("BIK_URL", "http://" + EXTERNAL_IP + ":" + bikService.getFirstMappedPort() + "/")
				.withEnv("DB_HOST", EXTERNAL_IP)
				.withEnv("RABBIT_HOST", EXTERNAL_IP)
				.withEnv("ZIPKIN_HOST", EXTERNAL_IP)
				.waitingFor(Wait.forHttp("/actuator/health").withStartupTimeout(Duration.ofSeconds(60)))
				.withLogConsumer(new Slf4jLogConsumer(log));
	}

	/**
	 * Aplikuje o pożyczkę.
	 * 
	 * @param fraudService kontener z fraud-service 
	 * @param payload ciało żądania HTTP
	 * @return odpowiedź na żądanie HTTP
	 */
	default ResponseEntity<String> applyForLoan(GenericContainer fraudService, String payload) {
		return new TestRestTemplate()
				.exchange(RequestEntity
						.post(URI.create("http://localhost:" + fraudService.getFirstMappedPort() + "/fraudCheck"))
						.contentType(MediaType.APPLICATION_JSON)
						.body(payload), String.class);
	}
	
	@AfterAll
	static void cleanup() {
		inetUtils.close();
	}
	
	
	static File compose() {
		try {
			return new File(composeForE2e.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

}
