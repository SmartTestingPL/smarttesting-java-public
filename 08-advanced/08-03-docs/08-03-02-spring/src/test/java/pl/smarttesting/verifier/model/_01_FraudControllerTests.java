package pl.smarttesting.verifier.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.smarttesting.customer.Customer;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.restdocs.SpringCloudContractRestDocs;
import org.springframework.cloud.contract.wiremock.restdocs.WireMockSnippet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Klasa testująca API HTTP. Wykorzystuje {@link MockMvc} w celu testowania API
 * bez faktycznego połączenia po warstwie sieciowej.
 */
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(classes = _01_FraudControllerTests.Config.class)
public class _01_FraudControllerTests {

	MockMvc mockMvc;

	/**
	 * Przed każdym uruchomieniem testu ustawiamy {@link MockMvc} na nowo.
	 * @param webApplicationContext - webowy kontekst aplikacyjny
	 * @param restDocumentation - rozszerzenie tworzący dokumentację naszego API
	 */
	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext,
			RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation))
				.build();
	}

	/**
	 * Prosty test weryfikujący nasze API dla przypadku oszusta.
	 */
	@Test
	void should_return_fraud() throws Exception {
		mockMvc.perform(post(URI.create("/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\n"
						+ "\"uuid\" : \"cc8aa8ff-40ff-426f-bc71-5bb7ea644108\",\n"
						+ "  \"person\" : {\n"
						+ "    \"name\" : \"Fraudeusz\",\n"
						+ "    \"surname\" : \"Fraudowski\",\n"
						+ "    \"dateOfBirth\" : \"01-01-1980\",\n"
						+ "    \"gender\" : \"MALE\",\n"
						+ "    \"nationalIdentificationNumber\" : \"2345678901\"\n"
						+ "  }\n"
						+ "}"))
				.andExpect(status().isUnauthorized());
	}

	/**
	 * Prosty test weryfikujący nasze API dla przypadku oszusta.
	 * Dodatkowo, tworzy dokumentację naszego API.
	 */
	@Test
	void should_return_fraud_with_rest_docs() throws Exception {
		mockMvc.perform(post(URI.create("/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\n"
						+ "\"uuid\" : \"cc8aa8ff-40ff-426f-bc71-5bb7ea644108\",\n"
						+ "  \"person\" : {\n"
						+ "    \"name\" : \"Fraudeusz\",\n"
						+ "    \"surname\" : \"Fraudowski\",\n"
						+ "    \"dateOfBirth\" : \"01-01-1980\",\n"
						+ "    \"gender\" : \"MALE\",\n"
						+ "    \"nationalIdentificationNumber\" : \"2345678901\"\n"
						+ "  }\n"
						+ "}"))
				.andExpect(status().isUnauthorized())
				// Dodajemy tworzenie dokumentacji
				.andDo(document("fraudCheck"));
	}

	/**
	 * Prosty test weryfikujący nasze API dla przypadku oszusta.
	 * Dodatkowo, tworzy dokumentację naszego API oraz kontraktów i zaślepek.
	 */
	@Test
	void should_return_fraud_with_rest_docs_and_spring_cloud_contract() throws Exception {
		mockMvc.perform(post(URI.create("/fraudCheck"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\n"
						+ "\"uuid\" : \"cc8aa8ff-40ff-426f-bc71-5bb7ea644108\",\n"
						+ "  \"person\" : {\n"
						+ "    \"name\" : \"Fraudeusz\",\n"
						+ "    \"surname\" : \"Fraudowski\",\n"
						+ "    \"dateOfBirth\" : \"01-01-1980\",\n"
						+ "    \"gender\" : \"MALE\",\n"
						+ "    \"nationalIdentificationNumber\" : \"2345678901\"\n"
						+ "  }\n"
						+ "}"))
				.andExpect(status().isUnauthorized())
				// Dodajemy tworzenie dokumentacji
				.andDo(document("fraudCheck",
						// oraz kontraktów
						SpringCloudContractRestDocs.dslContract(),
						// oraz zaślepek
						new WireMockSnippet()));
	}

	/**
	 * Konfiguracja testowa naszego API.
	 */
	@Configuration(proxyBeanMethods = false)
	@EnableAutoConfiguration
	static class Config {
		/**
		 * Kontroler ze sztuczną wersją {@link CustomerVerifier}. Testujemy TYLKO
		 * kontroler, nie chcemy przechodzić przez kolejne warstwy.
		 */
		@Bean FraudController fraudController() {
			return new FraudController(new CustomerVerifier(Collections.emptySet()) {

				/**
				 * Na potrzeby testów uznajemy, że pan Fraudowski reprezentuje oszusta.
				 * Każdy inny przypadek to osoba uczciwa.
				 */
				@Override
				List<VerificationResult> verify(Customer customer) {
					if (customer.getPerson().getSurname().equals("Fraudowski")) {
						return Collections.singletonList(new VerificationResult("test", false));
					}
					return Collections.singletonList(new VerificationResult("test", true));
				}
			});
		}
	}
}