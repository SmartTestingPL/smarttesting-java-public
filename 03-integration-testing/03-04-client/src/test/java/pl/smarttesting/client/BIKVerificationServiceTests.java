package pl.smarttesting.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.apache.http.ProtocolException;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.TestSocketUtils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Klasa testowa wykorzystująca ręcznie ustawione wartości połączenia po HTTP.
 * W tym przypadku, nasza implementacja BIKVerificationService ma nadpisaną metodę
 * pakietową, która zamiast logować wyjątek będzie go rzucać.
 *
 * W tej klasie testowej pokazujemy jak wysypała by się nasza aplikacja, gdybyśmy
 * odpowiednio nie obsłużyli w niej wyjątków.
 */
class BIKVerificationServiceTests {
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
	 * instancję klienta HTTP. Nadpisujemy też metodę, obsługującą rzucony wyjątek.
	 * W tym przypadku będziemy go ponownie rzucać.
	 */
	BIKVerificationService service = new BIKVerificationService("http://localhost:" + port + "/", httpClient()) {
		/**
		 * Symulujemy sytuację, w której nie obsłużyliśmy wyjątków
		 * @param exception - wyjątek do obsłużenia
		 */
		@Override
		void processException(IOException exception) {
			throw new IllegalStateException(exception);
		}
	};

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
	void should_fail_with_timeout() {
		// Zwracamy odpowiedź po 5 sekundach
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFixedDelay(5000)));

		// Oczekujemy, że zostanie rzucony wyjątek, związany z połączeniem HTTP
		// Gdyż według naszej konfiguracji połączenie HTTP powinno być nawiązane w
		// ciągu 1 sekundy.
		BDDAssertions.thenThrownBy(() ->
				service.verify(zbigniew()))
				.hasRootCauseInstanceOf(IOException.class);
	}

	// Ten i kolejne testy rzucają różne typy wyjątków i oczekujemy, że
	// wywołanie naszej metody biznesowej zakończy się rzuceniem wyjątku.
	@Test
	void should_fail_with_connection_reset_by_peer() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

		BDDAssertions.thenThrownBy(() ->
				service.verify(zbigniew()))
				.hasRootCauseInstanceOf(IOException.class);
	}

	@Test
	void should_fail_with_empty_response() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.EMPTY_RESPONSE)));

		BDDAssertions.thenThrownBy(() ->
				service.verify(zbigniew()))
				.hasRootCauseInstanceOf(IOException.class);
	}

	@Test
	void should_fail_with_malformed() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

		BDDAssertions.thenThrownBy(() ->
				service.verify(zbigniew()))
				.hasRootCauseInstanceOf(IOException.class);
	}

	@Test
	void should_fail_with_random() {
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

		BDDAssertions.thenThrownBy(() ->
				service.verify(zbigniew()))
				.hasRootCauseInstanceOf(ProtocolException.class);
	}

	private Customer zbigniew() {
		return new Customer(UUID.randomUUID(), youngZbigniew());
	}

	Person youngZbigniew() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "18210116954");
	}
}

