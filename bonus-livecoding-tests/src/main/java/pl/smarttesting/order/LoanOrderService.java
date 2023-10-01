package pl.smarttesting.order;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.db.MongoDbAccessor;
import pl.smarttesting.db.PostgresAccessor;
import pl.smarttesting.loan.LoanType;

import java.math.BigDecimal;
import java.time.LocalDate;

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
		LoanOrder loanOrder = new LoanOrder(now, customer, new BigDecimal(2000), new BigDecimal(5), new BigDecimal(200));
		loanOrder.setType(LoanType.STUDENT);
		BigDecimal discount = mongoDbAccessor.getPromotionDiscount("Student Promo");

		loanOrder.getPromotions()
				.add(new Promotion("Student Promo", discount));

		postgresDBAccessor.updatePromotionStatistics("Student Promo");
		return loanOrder;
	}
}