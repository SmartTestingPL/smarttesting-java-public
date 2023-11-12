package pl.smarttesting.order;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.loan.LoanType;

@Homework("Na pewno możemy popracować nad czytelnościa tych testów")
class LoanOrderServiceTest {

	@Test
	void testNotStudent() {
		LoanOrderService loanOrderService = new LoanOrderService(null, null);

		thenThrownBy(() -> loanOrderService.studentLoanOrder(new Customer(UUID.randomUUID(),
						new Person("A", "B", LocalDate.now(), GENDER.FEMALE, "1234567890"))))
				.hasMessageContaining("Cannot order student loan");
	}
	
	@Test
	void testStudent() {
		Customer customer = new Customer(UUID.randomUUID(),
				new Person("A", "B", LocalDate.now(), GENDER.FEMALE, "1234567890"));
		customer.student();
		MongoDbAccessor accessor = mock(MongoDbAccessor.class);
		given(accessor.getPromotionDiscount("Student Promo")).willReturn(new BigDecimal("100"));
		LoanOrderService loanOrderService = new LoanOrderService(mock(PostgresAccessor.class), accessor);
		LoanOrder studentLoanOrder = loanOrderService.studentLoanOrder(customer);
		then(studentLoanOrder.getType()).isEqualTo(LoanType.STUDENT);
		then(studentLoanOrder.getPromotions().get(0)).isEqualTo(new Promotion("Student Promo", new BigDecimal("100")));
		then(studentLoanOrder.getCommission()).isEqualTo(new BigDecimal("200"));
		then(studentLoanOrder.getAmount()).isEqualTo(new BigDecimal("500"));
	}

}