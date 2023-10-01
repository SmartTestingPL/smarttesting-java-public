package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.TestSocketUtils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * Klasa testowa wykorzystująca ręcznie ustawione wartości połączenia po HTTP.
 * W tym przypadku, domyślna implementacja BIKVerificationService, w przypadku błędu
 * zaloguje informacje o wyjątku.
 *
 * W tej klasie testowej pokazujemy jak powinniśmy przetestować naszego klienta HTTP.
 * Czy potrafimy obsłużyć wyjątki? Czy potrafimy obsłużyć scenariusze biznesowe?
 *
 * O problemach związanych z pisaniem zaślepek przez konsumenta API, będziemy mówić
 * w dalszej części szkolenia. Tu pokażemy ręczne zaślepianie scenariuszy
 * biznesowych.
 */
class BIKVerificationServiceNoIOExceptionsTests {
	/**
	 * Wybrano losowy otwarty port.
	 */
	int port = TestSocketUtils.findAvailableTcpPort();
	/**
	 * Do tworzenia zaślepek używamy biblioteki WireMock
	 */
	WireMockServer wireMockServer;
	/**
	 * Przez konstruktor wstrzykujemy adres naszego serwera WireMock, zamiast
	 * domyślnego serwera Biura Informacji Kredytowej. Podajemy też skonfigurowaną
	 * instancję klienta HTTP.
	 */
	BIKVerificationService service = new BIKVerificationService("http://localhost:" + port + "/", httpClient());

	/**
	 * W metodzie setup przed każdym testem uruchamiamy serwer WireMocka
	 * na danym porcie oraz mapujemy, że statyczne wywołanie metod
	 * z biblioteki WireMock zostanie wykonane wobec serwera działającego na
	 * wspomnianym porcie.
	 */
	@BeforeEach
	void setup() {
		wireMockServer = new WireMockServer(options().port(port));
		wireMockServer.start();
		WireMock.configureFor(port);
	}

	/**
	 * Konfigurujemy klienta HTTP nadpisując domyślną wartość Socket Timeout.
	 * Chcemy czekać na połączenie z serwerem maksymalnie 1 sekundę.
	 *
	 * @return klient HTTP
	 */
	private CloseableHttpClient httpClient() {
		return HttpClientBuilder.create()
				.setDefaultSocketConfig(SocketConfig.custom()
						.setSoTimeout(1000)
						.build())
				.build();
	}

	/**
	 * Po każdym teście zatrzymujemy serwer WireMock.
	 */
	@AfterEach
	void tearDown() {
		wireMockServer.stop();
	}

	@Test
	void should_return_positive_verification() {
		// Zaślepiamy wywołanie GET, zwracając odpowiednią wartość tekstową
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withBody("VERIFICATION_PASSED")));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_PASSED);
	}

	@Test
	void should_return_negative_verification() {
		// Zaślepiamy wywołanie GET, zwracając odpowiednią wartość tekstową
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withBody("VERIFICATION_FAILED")));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	// W tym i kolejnych testach zaślepiamy wywołanie GET zwracając różne
	// błędy techniczne. Chcemy się upewnić, że potrafimy je obsłużyć.
	@Test
	void should_fail_with_connection_reset_by_peer() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	@Test
	void should_fail_with_empty_response() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.EMPTY_RESPONSE)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	@Test
	void should_fail_with_malformed() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	@Test
	void should_fail_with_random() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

		then(service.verify(zbigniew()).getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	private Customer zbigniew() {
		return new Customer(UUID.randomUUID(), youngZbigniew());
	}

	Person youngZbigniew() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "18210116954");
	}
}

