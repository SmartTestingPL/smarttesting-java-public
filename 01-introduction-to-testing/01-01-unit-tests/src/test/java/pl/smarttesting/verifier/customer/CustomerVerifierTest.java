package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;
import pl.smarttesting.verifier.customer.verification.AgeVerification;
import pl.smarttesting.verifier.customer.verification.IdentificationNumberVerification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Klasa zawiera przykłady inicjalizacji w polach testowych, przykład false-positive,
 * przykład zastosowania Test Doubles.
 *
 * Suite'a testów zawiera test na przypadek negatywny: `CustomerVerifierTest.shouldFailSimpleVerification`,
 * ale nie zawiera tesów weryfikujących pozytywną weryfikację, przez co testy nie wychwytują,
 * że kod produkcyjny zwraca domyślną wartość i brakuje implementacji logiki biznesowej
 */
class CustomerVerifierTest {

	// W zależności od używanego frameworku inicjalizacja w polach może być stanowa
	// dla metody testowej (świeży stan dla wywołania każdej metody) lub na cały test.
	CustomerVerifier service = new CustomerVerifier(new TestVerificationService(), buildVerifications(), new TestBadServiceWrapper());

	@Test
	void shouldVerifyCorrectPerson() {
		// Given
		Customer customer = buildCustomer();

		// When
		CustomerVerificationResult result = service.verify(customer);

		// Then
		assertThat(result.getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_PASSED);
		assertThat(result.getUserId()).isEqualTo(customer.getUuid());
	}

	// Test weryfikuje przypadek negatywnej weryfikacji, ale w klasie zabrakło testu na pozytywną
	// weryfikację klienta. Przez to testy nie wychwytują, że kod produkcyjny zwraca domyślną wartość
	// i brakuje implementacji logiki biznesowej.
	@Test
	void shouldFailSimpleVerification() {
		// Given
		Customer customer = buildCustomer();
		CustomerVerifier service = new CustomerVerifier(new TestVerificationService(),
				buildSimpleVerification(), new TestBadServiceWrapper());

		// When
		CustomerVerificationResult result = service.verify(customer);

		assertThat(result.getStatus())
				.isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	private Set<Verification> buildSimpleVerification() {
		Set<Verification> verifications = new HashSet<>();
		verifications.add(new SimpleVerification());
		return verifications;
	}

	private Customer buildCustomer() {
		return new Customer(UUID.randomUUID(), new Person("John", "Smith",
				LocalDate.of(1996, 8, 28), Person.GENDER.MALE,
				"96082812079"));
	}

	private Set<Verification> buildVerifications() {
		Set<Verification> verifications = new HashSet<>();
		verifications.add(new AgeVerification());
		verifications.add(new IdentificationNumberVerification());
		return verifications;
	}

	// Implementacja testowa (Test Double) w celu uniknięcia kontaktowania się z
	// zewnętrznym serwisem w testach jednostkowych.
	static class TestVerificationService extends BIKVerificationService {

		public TestVerificationService() {
			super("http://example.com");
		}

		@Override
		public CustomerVerificationResult verify(Customer customer) {
			return CustomerVerificationResult.passed(customer.getUuid());
		}
	}

	// Implementacja testowa (Test Double) w celu uniknięcia kontaktowania się z
	// zewnętrznym serwisem w testach jednostkowych.
	static class TestBadServiceWrapper extends VeryBadVerificationServiceWrapper {
		@Override
		public boolean verify() {
			// do not run all these have database and network operations in a unit test
			return true;
		}
	}

}

