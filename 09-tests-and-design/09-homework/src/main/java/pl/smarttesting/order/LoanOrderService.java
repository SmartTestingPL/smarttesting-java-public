package pl.smarttesting.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.loan.LoanType;

/**
 * Interfejs służący do komunikacji z relacyjną bazą danych.
 * Posłuży nam do przykładów zastosowania mocków i weryfikacji interakcji.
 */
interface PostgresAccessor {

	void updatePromotionStatistics(String promotionName);

	void updatePromotionDiscount(String promotionName, BigDecimal newDiscount);
}

/**
 * Interfejs służący do komunikacji z dokumentową bazą danych.
 * Posłuży nam do przykładów zastosowania stubów.
 */
interface MongoDbAccessor {

	BigDecimal getPromotionDiscount(String promotionName);

}

/**
 * Serwis procesujący przynawanie pożyczek w zależności od typu pożyczki i obowiązujących promocji.
 */
public class LoanOrderService {

	private final PostgresAccessor postgresDBAccessor;
	private final MongoDbAccessor mongoDbAccessor;

	public LoanOrderService(PostgresAccessor postgresDBAccessor, MongoDbAccessor mongoDbAccessor) {
		this.postgresDBAccessor = postgresDBAccessor;
		this.mongoDbAccessor = mongoDbAccessor;
	}

	public LoanOrder studentLoanOrder(Customer customer) {
		if (!customer.isStudent()) {
			throw new IllegalStateException("Cannot order student loan if customer is not a student.");
		}
		LocalDate now = LocalDate.now();
		LoanOrder loanOrder = new LoanOrder(now, customer);
		loanOrder.setType(LoanType.STUDENT);
		BigDecimal discount = mongoDbAccessor.getPromotionDiscount("Student Promo");

		loanOrder.getPromotions()
				.add(new Promotion("Student Promo", discount));
		loanOrder.setCommission(new BigDecimal("200"));
		loanOrder.setAmount(new BigDecimal("500"));
		postgresDBAccessor.updatePromotionStatistics("Student Promo");
		return loanOrder;
	}
}