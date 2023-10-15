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

	@Test
	void should_mark_custom_as_fraud(@LocalServerPort int port) {
		ResponseEntity<Void> response = new TestRestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(FRAUD), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void should_mark_custom_as_non_fraud(@LocalServerPort int port) {
		ResponseEntity<Void> response = new TestRestTemplate().exchange(RequestEntity
				.post(URI.create("http://localhost:" + port + "/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.body(NON_FRAUD), Void.class);

		then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
