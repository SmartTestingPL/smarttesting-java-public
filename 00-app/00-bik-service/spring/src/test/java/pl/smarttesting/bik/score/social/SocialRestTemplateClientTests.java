package pl.smarttesting.bik.score.social;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import io.github.resilience4j.spring6.timelimiter.configure.TimeLimiterConfigurationProperties;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import io.github.resilience4j.springboot3.timelimiter.autoconfigure.TimeLimiterConfigurationOnMissingBean;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.TestSocketUtils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

// Dotyczy lekcji 03-04
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SocialRestTemplateClientTests.TestConfig.class)
@AutoConfigureWebClient
class SocialRestTemplateClientTests {

	static int port = TestSocketUtils.findAvailableTcpPort();

	static WireMockServer wireMockServer = new WireMockServer(options().port(port));

	@BeforeAll
	static void setup() {
		wireMockServer.start();
		WireMock.configureFor(port);
	}

	@AfterAll
	static void tearDown() {
		wireMockServer.stop();
	}

	@Autowired
	SocialRestTemplateClient service;

	@Test
	void should_fail_with_timeout() {
		WireMock.stubFor(WireMock.get("/")
				.willReturn(WireMock.aResponse().withFixedDelay(10000)));

		BDDAssertions.thenThrownBy(() ->
						service.get("http://localhost:" + port + "/", String.class))
				.hasRootCauseInstanceOf(TimeoutException.class);
	}

	@Test
	void should_fail_with_connection_reset_by_peer() {
		WireMock.stubFor(WireMock.get("/")
				.willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

		BDDAssertions.thenThrownBy(() ->
						service.get("http://localhost:" + port + "/", String.class))
				.isInstanceOf(NoFallbackAvailableException.class)
				.hasRootCauseInstanceOf(IOException.class);
	}

	@Test
	void should_fail_with_empty_response() {
		WireMock.stubFor(WireMock.get("/")
				.willReturn(WireMock.aResponse().withFault(Fault.EMPTY_RESPONSE)));

		BDDAssertions.thenThrownBy(() ->
						service.get("http://localhost:" + port + "/", String.class))
				.isInstanceOf(NoFallbackAvailableException.class)
				.hasRootCauseInstanceOf(IOException.class);
	}

	@Test
	void should_fail_with_malformed() {
		WireMock.stubFor(WireMock.get("/")
				.willReturn(WireMock.aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

		BDDAssertions.thenThrownBy(() ->
						service.get("http://localhost:" + port + "/", String.class))
				.isInstanceOf(NoFallbackAvailableException.class)
				.hasRootCauseInstanceOf(IOException.class);
	}

	@Test
	void should_fail_with_random() {
		WireMock.stubFor(WireMock.get("/")
				.willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

		BDDAssertions.thenThrownBy(() ->
						service.get("http://localhost:" + port + "/", String.class))
				.isInstanceOf(NoFallbackAvailableException.class);
	}

	@TestConfiguration(proxyBeanMethods = false)
	@ImportAutoConfiguration(classes = { CircuitBreakerAutoConfiguration.class, Resilience4JAutoConfiguration.class})
	@Import({ TimeLimiterConfigurationOnMissingBean.class, SocialConfiguration.class})
	static class TestConfig {

		@Bean
		TimeLimiterConfigurationProperties timeLimiterConfigurationProperties() {
			return new TimeLimiterConfigurationProperties();
		}

	}
}
