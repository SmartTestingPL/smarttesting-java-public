package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.VerificationEvent;
import pl.smarttesting.verifier.customer.verification.AgeVerification;
import pl.smarttesting.verifier.customer.verification.NameVerification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Klasa zawiera przykłady zastosowania mocka w celu weryfikacji interakcji z obiektem typu EventEmitter,
 * przykłady zastosowania buildera obiektów testowych, przykłady testowania komunikacji/interakcji.
 */
class CustomerVerifierTest extends CustomerTestBase {

	private EventEmitter eventEmitter;
	private CustomerVerifier customerVerifier;
	private Customer customer;

	@BeforeEach
	void setUp() {
		customer = buildCustomer();
		// Tworzenie mocka obiektu typu EventEmitter
		eventEmitter = mock(EventEmitter.class);
		customerVerifier = new CustomerVerifier(buildVerifications(eventEmitter));
	}


	private Set<Verification> buildVerifications(EventEmitter eventEmitter) {
		Set<Verification> verifications = new HashSet<>();
		verifications.add(new AgeVerification(eventEmitter));
		verifications.add(new NameVerification(eventEmitter));
		return verifications;
	}

	// Zastosowanie buildera w setupie testu.
	@Test
	void shouldVerifyCorrectPerson() {
		customer = CustomerTestBase.builder()
				.withNationalIdentificationNumber("80030818293")
				.withDateOfBirth(1980, 3, 8)
				.withGender(Person.GENDER.MALE)
				.build();
		CustomerVerificationResult result = customerVerifier.verify(customer);

		assertThat(result.getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_PASSED);
		assertThat(result.getUserId()).isEqualTo(customer.getUuid());
	}

	// Metoda pomocnicza do setupu testów.
	private Customer buildCustomer() {
		return new Customer(UUID.randomUUID(), new Person("John", "Smith",
				LocalDate.of(1996, 8, 28), Person.GENDER.MALE,
				"96082812079"));
	}
}

