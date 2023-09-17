package pl.smarttesting.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.loan.LoanType;

/**
 * Serwis procesujący przynawanie pożyczek w zależności od typu pożyczki i obowiązujących promocji.
 */
public class LoanOrderService {

	public LoanOrder studentLoanOrder(Customer customer) {
		if (!customer.isStudent()) {
			throw new IllegalStateException("Cannot order student loan if pl.smarttesting.customer is not a student.");
		}
		LocalDate now = LocalDate.now();
		LoanOrder loanOrder = new LoanOrder(now, customer);
		loanOrder.setType(LoanType.STUDENT);
		loanOrder.getPromotions()
				.add(new Promotion("Student Promo", new BigDecimal(10)));
		loanOrder.setCommission(new BigDecimal(200));
		return loanOrder;
	}
}
