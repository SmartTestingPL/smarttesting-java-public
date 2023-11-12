package pl.smarttesting.wrongtests;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class _02_DoesMockitoWorkTests {

	/**
	 * W tym teście de facto weryfikujemy czy framework do mockowania działa.
	 */
	@Test
	void should_return_positive_fraud_verification_when_fraud() {
		AnotherFraudService service = mock(AnotherFraudService.class);
		given(service.isFraud(any())).willReturn(true);

		BDDAssertions.then(new FraudService(service).checkIfFraud(new Person())).isTrue();
	}

}

class FraudService {
	private final AnotherFraudService service;

	FraudService(AnotherFraudService service) {
		this.service = service;
	}

	boolean checkIfFraud(Person person) {
		return this.service.isFraud(person);
	}
}

interface AnotherFraudService {
	boolean isFraud(Person person);
}

class Person {

}