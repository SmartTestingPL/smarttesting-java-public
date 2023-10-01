package pl.smarttesting.loan;

import pl.smarttesting.loan.validation.LoanValidationException;
import pl.smarttesting.order.LoanOrder;
import pl.smarttesting.order.Promotion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

public class Loan {

	private final LocalDate loanOpenedDate;

	private final BigDecimal amount;
	private final int numberOfInstallments;
	private final BigDecimal installmentAmount;
	private final UUID uuid;

	public Loan(LocalDate loanOpenedDate, LoanOrder loanOrder, int numberOfInstallments) {
		this.loanOpenedDate = loanOpenedDate;
		this.amount = calculateLoanAmount(loanOrder);
		this.numberOfInstallments = numberOfInstallments;
		installmentAmount = amount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_EVEN);
		uuid = UUID.randomUUID();
	}

	public Loan(LoanOrder loanOrder, int numberOfInstallments) {
		this(LocalDate.now(), loanOrder, numberOfInstallments);
	}

	private BigDecimal calculateLoanAmount(LoanOrder loanOrder) {
		validateElement(loanOrder.getAmount());
		validateElement(loanOrder.getInterestRate());
		validateElement(loanOrder.getCommission());
		BigDecimal interestFactor = new BigDecimal(1).add(loanOrder.getInterestRate().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
		BigDecimal baseAmount = (loanOrder.getAmount().multiply(interestFactor)).add(loanOrder.getCommission());
		return applyPromotionDiscounts(loanOrder, baseAmount);
	}

	private BigDecimal applyPromotionDiscounts(LoanOrder loanOrder, BigDecimal baseAmount) {
		BigDecimal discountSum = loanOrder.getPromotions()
				.stream()
				.map(Promotion::discount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal fifteenPercentOfBaseSum = baseAmount.multiply(new BigDecimal(15)).divide(new BigDecimal(100), RoundingMode.HALF_EVEN);
		if (fifteenPercentOfBaseSum.compareTo(discountSum) <= 0) {
			return baseAmount.subtract(fifteenPercentOfBaseSum);
		}
		return baseAmount.subtract(discountSum);
	}

	public BigDecimal getInstallmentAmount() {
		return installmentAmount;
	}

	public UUID getUuid() {
		return uuid;
	}

	private void validateElement(BigDecimal elementAmount) {
		if (elementAmount == null || elementAmount.compareTo(BigDecimal.ONE) < 0) {
			throw new LoanValidationException();
		}
	}

	public LocalDate getLoanOpenedDate() {
		return loanOpenedDate;
	}

	public int getNumberOfInstallments() {
		return numberOfInstallments;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
