package pl.smarttesting.verifier.model;

import java.util.UUID;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;

/**
 * Testy do klasy {@link BIKVerificationService}.
 */
class BIKVerificationServiceTests {

	@Test
	void should_return_successful_verification() {
		BIKVerificationService service = new BIKVerificationService("");

		CustomerVerificationResult result = service.verify(new Customer(UUID.randomUUID(), null));

		BDDAssertions.then(result.passed()).isTrue();
	}

	@Test
	void should_return_failed_verification() {
		BIKVerificationService service = new BIKVerificationService("") {
			@Override
			CustomerVerificationResult pass(Customer customer) {
				throw new IllegalStateException("Boom!");
			}
		};

		CustomerVerificationResult result = service.verify(new Customer(UUID.randomUUID(), null));

		BDDAssertions.then(result.passed()).isFalse();
	}

	@Test
	void should_not_blow_up_due_to_cyclomatic_complexity() {
		BIKVerificationService service = new BIKVerificationService("");

		int result = service.complexMethod(1, 2, 3);

		BDDAssertions.then(result).isEqualTo(8);
	}
}