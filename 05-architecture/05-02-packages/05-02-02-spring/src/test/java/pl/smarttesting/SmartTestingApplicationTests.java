package pl.smarttesting;

import java.net.URI;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Test end to end naszej aplikacji. Uruchamiamy serwer HTTP na dowolnym porcie,
 * w trybie deweloperskim ({@link ActiveProfiles} ma wartość dev), dzięki czemu nie potrzebujemy
 * baz danych, brokerów wiadomości i mamy zaślepioną komunikację z serwisami zewnętrznymi.
 * Testujemy czy potrafimy obsłużyć dwa przypadki testowe: ktoś jest oszustem i ktoś nim nie jest.
 * W ten sposób przechodzimy przez ścieżki krytyczne naszej aplikacji.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class SmartTestingApplicationTests {

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

	private static final String NON_FRAUD = "{\n"
			+ "  \"uuid\" : \"89c878e3-38f7-4831-af6c-c3b4a0669022\",\n"
			+ "  \"person\" : {\n"
			+ "    \"name\" : \"Stefania\",\n"
			+ "    \"surname\" : \"Stefanowska\",\n"
			+ "    \"dateOfBirth\" : \"01-01-2020\",\n"
			+ "    \"gender\" : \"FEMALE\",\n"
			+ "    \"nationalIdentificationNumber\" : \"1234567890\"\n"
			+ "  }\n"
			+ "}";

	/**
	 * Testujemy scenariusz krytyczny weryfikacji oszusta.
	 * Dla oszusta oczekujemy, że status odpowiedzi będzie 401 - UNAUTHORIZED
	 * @param port - losowy port, na którym uruchomiła się nasza aplikacja
	 */
	@Test
	void should_mark_custom_as_fraud(@LocalServerPort int port) {
		ResponseEntity<Void> response = new TestRestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(FRAUD), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Testujemy scenariusz krytyczny weryfikacji osoby uczciwej.
	 * Dla osoby uczciwej oczekujemy, że status odpowiedzi będzie 200 - OK
	 * @param port - losowy port, na którym uruchomiła się nasza aplikacja
	 */
	@Test
	void should_mark_custom_as_non_fraud(@LocalServerPort int port) {
		ResponseEntity<Void> response = new TestRestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(NON_FRAUD), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
