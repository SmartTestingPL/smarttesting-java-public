package pl.smarttesting.loan;

import org.junit.jupiter.api.Test;
import pl.smarttesting.order.LoanOrder;
import pl.smarttesting.order.Promotion;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.smarttesting.loan.LoanTestUtils.aLoanOrder;

class LoanTest {

	private static final BigDecimal AMOUNT = new BigDecimal(3000);
	private static final BigDecimal INTEREST_RATE = new BigDecimal(5);
	private static final BigDecimal COMMISSION = new BigDecimal(200);

	@Test
	void shouldCreateLoan() {
//		LoanOrder loanOrder = aLoanOrder(new BigDecimal(3000), new BigDecimal(5), new BigDecimal(200));
		LoanOrder loanOrder = aLoanOrder(AMOUNT, INTEREST_RATE, COMMISSION);

		Loan loan = new Loan(loanOrder, 6);

		assertThat(loan.getLoanOpenedDate()).isToday();
		assertThat(loan.getNumberOfInstallments()).isEqualTo(6);
		assertThat(loan.getAmount()).isEqualTo("3350.00");
	}

	@Test
	void shouldCalculateInstallmentAmount() {
		LoanOrder loanOrder = aLoanOrder(AMOUNT, INTEREST_RATE, COMMISSION);

		BigDecimal loanInstallment = new Loan(loanOrder, 6).getInstallmentAmount();

		assertThat(loanInstallment).isEqualByComparingTo("558.33");
	}

	@Test
	void shouldApplyPromotionDiscount() {
		LoanOrder loanOrder = aLoanOrder(AMOUNT, INTEREST_RATE, COMMISSION, new Promotion("Test 10", new BigDecimal(10)
		), new Promotion("test 20", new BigDecimal(20)));

		Loan loan = new Loan(loanOrder, 6);

		assertThat(loan.getAmount()).isEqualTo("3320.00");
		assertThat(loan.getInstallmentAmount()).isEqualTo("553.33");
	}

	@Test
	void shouldApplyFixedDiscountIfPromotionDiscountSumHigherThanThreshold() {
		LoanOrder loanOrder = aLoanOrder(new BigDecimal(2000), new BigDecimal(5), new BigDecimal(300),
				new Promotion("61", new BigDecimal(61)), new Promotion("300", new BigDecimal(300)));

		// Base amount: 2400
		BigDecimal loanAmount = new Loan(loanOrder, 6).getAmount();

		assertThat(loanAmount).isEqualTo("2040.00");
	}

}