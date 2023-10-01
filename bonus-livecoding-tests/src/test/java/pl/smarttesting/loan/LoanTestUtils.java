package pl.smarttesting.loan;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import pl.smarttesting.order.LoanOrder;
import pl.smarttesting.order.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

final class LoanTestUtils {

	private LoanTestUtils() {
		throw new IllegalStateException("Should not instantiate utility class");
	}

	static LoanOrder aLoanOrder(BigDecimal amount, BigDecimal interestRate, BigDecimal commission,
								Promotion... promotions) {
		Customer customer = new Customer(UUID
				.randomUUID(), new Person("Maria", "Kowalska",
				LocalDate.of(1989, 3, 10), Person.GENDER.FEMALE,
				"89031013409"));
		LoanOrder loanOrder = new LoanOrder(LocalDate.now(), customer, amount, interestRate, commission);
		loanOrder.getPromotions().addAll(Arrays.asList(promotions));
		return loanOrder;
	}

	static LoanOrder aLoanOrder(BigDecimal amount, BigDecimal interestRate, BigDecimal commission) {
		Customer customer = new Customer(UUID
				.randomUUID(), new Person("Maria", "Kowalska",
				LocalDate.of(1989, 3, 10), Person.GENDER.FEMALE,
				"89031013409"));
		return new LoanOrder(LocalDate.now(), customer, amount, interestRate, commission);
	}

	static LoanOrder aLoanOrder() {
		return aLoanOrder(new BigDecimal(2000), new BigDecimal(5), new BigDecimal(300));
	}

	static LoanOrder aLoanOrder(Promotion... promotions) {
		return aLoanOrder(new BigDecimal(2000), new BigDecimal(5), new BigDecimal(300), promotions);
	}
}
