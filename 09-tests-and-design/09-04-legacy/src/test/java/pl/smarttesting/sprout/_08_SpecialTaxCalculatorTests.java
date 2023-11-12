package pl.smarttesting.sprout;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class _08_SpecialTaxCalculatorTests {

	@Test
	void should_not_apply_special_tax_when_amount_not_reaching_threshold() {
		int initialAmount = 8;
		SpecialTaxCalculator calculator = new SpecialTaxCalculator(initialAmount);

		then(calculator.calculate())
				.isEqualTo(initialAmount);
	}

	@Test
	void should_apply_special_tax_when_amount_reaches_threshold() {
		int initialAmount = 25;
		SpecialTaxCalculator calculator = new SpecialTaxCalculator(initialAmount);

		then(calculator.calculate())
				.isEqualTo(500);
	}
}


class SpecialTaxCalculator {
	static final int AMOUNT_THRESHOLD = 10;
	static final int TAX_MULTIPLIER = 20;
	private final int amount;

	SpecialTaxCalculator(int amount) {
		this.amount = amount;
	}

	int calculate() {
		if (amount <= AMOUNT_THRESHOLD) {
			return amount;
		}
		return amount * TAX_MULTIPLIER;
	}
}
