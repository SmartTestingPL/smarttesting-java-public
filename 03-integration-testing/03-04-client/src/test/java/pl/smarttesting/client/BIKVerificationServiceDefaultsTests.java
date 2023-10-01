package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.test.util.TestSocketUtils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.BDDAssertions.then;

/**
 * Klasa testowa wykorzystująca wartości domyślne w konfiguracji biblioteki
 * do tworzenia połączeń po HTTP.
 */
class BIKVerificationServiceDefaultsTests {
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
	 * domyślnego serwera Biura Informacji Kredytowej.
	 */
	BIKVerificationService service = new BIKVerificationService("http://localhost:" + port + "/");

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
	 * Po każdym teście zatrzymujemy serwer WireMock.
	 */
	@AfterEach
	void tearDown() {
		wireMockServer.stop();
	}

	/**
	 * Jeśli odkomentujemy ten test - nigdy się nie zakończy. Dlatego, że wartości
	 * domyślne w org.apache.http.config.SocketConfig.getSoTimeout , które są używane
	 * przez klienta HTTP zakładają, że SocketTimeout ma nieskończoną wartość.
	 */
//	@Test
	void should_fail_with_connection_reset_by_peer() {
		// Mówimy zaślepce serwera HTTP, żeby odpowiedziała błędem resetującym połączenie.
		// Dobrze skonfigurowany klient HTTP powinien rzucić wyjątkiem po określonym czasie.
		WireMock.stubFor(WireMock.get("/18210116954")
				.willReturn(WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

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

