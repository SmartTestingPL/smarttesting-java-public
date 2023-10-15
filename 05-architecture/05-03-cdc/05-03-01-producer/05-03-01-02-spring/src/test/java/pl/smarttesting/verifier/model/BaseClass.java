package pl.smarttesting.verifier.model;

import java.util.UUID;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import pl.smarttesting.customer.Customer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;

/**
 * W przypadku Javy, Spring Cloud Contract posiada natywne wsparcie i nie musimy
 * operować obrazami Dockerowymi.
 *
 * Klasa ta będzie rozszerzona przez wygenerowane klasy testowe. Przygotowujemy środowisko
 * przy użyciu MockMvc (mockujemy warstwę sieciową) i przekazujemy kontroler, który chcemy przetestować.
 * Kontroler będzie używał mocka serwisu aplikacyjnego, ponieważ nie chcemy testować prawdziwej
 * logiki biznesowej tylko poprawność komunikacji.
 */
public abstract class BaseClass {
	@BeforeEach
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(new FraudController(mockedCustomerVerifier()));
	}

	/**
	 * Chcemy testować tylko kontroler, dlatego mockujemy serwis aplikacyjny.
	 * Symulujemy dwa przypadki testowe, jeden pozytywny (osoba nas nie chce oszukać) oraz
	 * drugi, negatywny, gdzie osoba jest oszustem (o nazwisku Fraudeusz).
	 * @return zamockowany serwis aplikacyjny
	 */
	private CustomerVerifier mockedCustomerVerifier() {
		CustomerVerifier verifier = BDDMockito.mock(CustomerVerifier.class);
		given(verifier.verify(any(Customer.class)))
				.willReturn(CustomerVerificationResult.passed(
				UUID.fromString("89c878e3-38f7-4831-af6c-c3b4a0669022")));
		given(verifier.verify(argThat(customer -> customer.getPerson().getName().equals("Fraudeusz"))))
				.willReturn(CustomerVerificationResult.failed(
						UUID.fromString("cc8aa8ff-40ff-426f-bc71-5bb7ea644108")));
		return verifier;
	}
}
