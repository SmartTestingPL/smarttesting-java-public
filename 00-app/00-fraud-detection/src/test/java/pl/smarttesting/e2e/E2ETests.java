package pl.smarttesting.e2e;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.smarttesting.e2e.ContainerRunning.infrastructure;

/**
 * Testy end to end, stawiające infrastrukturę oraz obie aplikacje.
 */
// Dotyczy lekcji 4.1.2
@Testcontainers
@Tag("e2e")
@SuppressWarnings("rawtypes")
class E2ETests implements ContainerRunning {

	/**
	 * Wpierw stawiamy infrastrukturę.
	 */
	@Container
	static DockerComposeContainer container = infrastructure();

	/**
	 * Następnie stawiamy bik-service. Musi być uruchomiony jako pierwszy, ponieważ fraud-detection będzie potrzebował
	 * jego URLa.
	 */
	@Container
	GenericContainer bikService = latestBikService();

	/**
	 * Przykład żądania HTTP dla oszusta.
	 */
	static final String FRAUD = """
			{
			  "uuid" : "cc8aa8ff-40ff-426f-bc71-5bb7ea644108",
			  "person" : {
			    "name" : "Fraudeusz",
			    "surname" : "Fraudowski",
			    "dateOfBirth" : "01-01-1980",
			    "gender" : "MALE",
			    "nationalIdentificationNumber" : "00262161334"
			  }
			}
			""";

	/**
	 * Przykład żądania HTTP dla osoby nie będącej oszustem.
	 */
	static final String NON_FRAUD = """
			{
			  "uuid" : "89c878e3-38f7-4831-af6c-c3b4a0669022",
			  "person" : {
			    "name" : "Stefania",
			    "surname" : "Stefanowska",
			    "dateOfBirth" : "01-05-1989",
			    "gender" : "FEMALE",
			    "nationalIdentificationNumber" : "89050193724"
			  }
			}
			""";

	@Disabled("Testy nie przejda bo mamy bledy w implementacji")
	@Test
	void should_return_proper_statuses_for_fraud_and_non_fraud() {
		try (GenericContainer fraudService = startFraudService(bikService)) {
			ResponseEntity<String> entity = applyForLoan(fraudService, FRAUD);

			then(entity.getStatusCode()).isEqualTo(UNAUTHORIZED);

			entity = applyForLoan(fraudService, NON_FRAUD);

			then(entity.getStatusCode()).isEqualTo(OK);
		}
	}

}
