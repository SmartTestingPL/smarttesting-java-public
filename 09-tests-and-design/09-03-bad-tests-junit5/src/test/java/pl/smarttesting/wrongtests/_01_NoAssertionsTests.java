package pl.smarttesting.wrongtests;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

public class _01_NoAssertionsTests {

	/**
	 * Test bez asercji.
	 */
	@Test
	void should_return_sum_when_adding_two_numbers() {
		int firstNumber = 1;
		int secondNumber = 2;

		int result = firstNumber + secondNumber;

		thenTwoNumbersShouldBeAdded(result);
	}

	private void thenTwoNumbersShouldBeAdded(int result) {
		// brakuje asercji!
		BDDAssertions.then(result)
				.as("should be equal to 3");
	}

	/**
	 * Poprawiony test składający się z samej asercji.
	 */
	@Test
	void should_return_sum_when_adding_two_numbers_correct() {
		BDDAssertions.then(1 + 2).isEqualTo(3);
	}

}
