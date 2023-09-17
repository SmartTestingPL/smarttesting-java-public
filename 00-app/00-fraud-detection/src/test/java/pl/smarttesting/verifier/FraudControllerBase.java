package pl.smarttesting.verifier;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.smarttesting.customer.Customer;

import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

// Dotyczy lekcji 08-03
@ExtendWith(RestDocumentationExtension.class)
// Dotyczy lekcji 05-03
public abstract class FraudControllerBase {

	@BeforeEach
	// Dotyczy lekcji 08-03
	void setup(RestDocumentationContextProvider provider, TestInfo testInfo) {

		// Dotyczy lekcji 08-03 i wywalić to poniżej (kod poniżej zastępuje ten z końca metody)
		RestAssuredMockMvc.mockMvc(MockMvcBuilders.standaloneSetup(new FraudController(customerVerifier(), new SimpleMeterRegistry()))
			.apply(documentationConfiguration(provider))
			.alwaysDo(document(getClass().getSimpleName() + "_" + testInfo.getDisplayName()))
			.build());

		// Dotyczy lekcji 05-03 (kod z lekcji 08-03 zastępuje ten poniżej)
		// RestAssuredMockMvc.standaloneSetup(new FraudController(customerVerifier()));
	}

	private CustomerVerifier customerVerifier() {
		return new CustomerVerifier(null, new SimpleObjectProvider<>(null), null, null, new SimpleMeterRegistry()) {
			@Override
			CustomerVerificationResult verify(Customer customer) {
				if (customer.getPerson().getName().equals("Stefania")) {
					return CustomerVerificationResult.passed(customer.getUuid());
				}
				return CustomerVerificationResult.failed(customer.getUuid());
			}
		};
	}
}
