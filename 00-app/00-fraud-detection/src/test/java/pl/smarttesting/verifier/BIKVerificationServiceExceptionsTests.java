package pl.smarttesting.verifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;
import static pl.smarttesting.verifier.CustomerVerificationResult.Status.VERIFICATION_FAILED;
import static pl.smarttesting.verifier.CustomerVerificationResult.Status.VERIFICATION_PASSED;

// Dotyczy lekcji 03-04
@WireMockTest
class BIKVerificationServiceExceptionsTests {

	BIKVerificationService service;

	@BeforeEach
	void setup(WireMockRuntimeInfo wmRuntimeInfo) {
		service = new BIKVerificationService("http://localhost:" + wmRuntimeInfo.getHttpPort(), httpClient());
	}

	private RestTemplate httpClient() {
		return new RestTemplateBuilder()
				.setConnectTimeout(Duration.ofMillis(1000))
				.build();
	}

	@Test
	void should_return_positive_verification() {
		// Zaślepiamy wywołanie GET, zwracając odpowiednią wartość tekstową
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withBody("{ \"status\" : \"VERIFICATION_PASSED\" }")
					.withHeader("Content-Type", "application/json")));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(VERIFICATION_PASSED);
	}

	@Test
	void should_return_negative_verification() {
		// Zaślepiamy wywołanie GET, zwracając odpowiednią wartość tekstową
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withBody("{ \"status\" : \"VERIFICATION_FAILED\" }")));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(VERIFICATION_FAILED);
	}

	// W tym i kolejnych testach zaślepiamy wywołanie GET zwracając różne
	// błędy techniczne. Chcemy się upewnić, że potrafimy je obsłużyć.
	@Test
	void should_fail_with_connection_reset_by_peer() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(VERIFICATION_FAILED);
	}

	@Test
	void should_fail_with_empty_response() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.EMPTY_RESPONSE)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(VERIFICATION_FAILED);
	}

	@Test
	void should_fail_with_malformed() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(VERIFICATION_FAILED);
	}

	@Test
	void should_fail_with_random() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(VERIFICATION_FAILED);
	}

	private Customer zbigniew() {
		return new Customer(UUID.randomUUID(), youngZbigniew());
	}

	Person youngZbigniew() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "18210116954");
	}
}

