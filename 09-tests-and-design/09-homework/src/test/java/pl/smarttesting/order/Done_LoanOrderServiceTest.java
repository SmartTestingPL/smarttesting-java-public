package pl.smarttesting.order;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.loan.LoanType;

/**
 * Pierwotny test jest mało czytelny. Po pierwsze nazwy metod są niedokładne, a
 * po drugie w możemy lepiej kod sformatować i zastosować assert object, żeby
 * zwiększyć czytelność sekcji then.
 */
class Done_LoanOrderServiceTest {

	private static final String STUDENT_PROMO_DISCOUNT_NAME = "Student Promo";

	private static final String DEFAULT_STUDENT_PROMO_DISCOUNT_VALUE = "100";
	
	private static final String DEFAULT_STUDENT_PROMO_COMMISSION_VALUE = "200";
	
	private static final String DEFAULT_STUDENT_PROMO_LOAN_AMOUNT = "500";

	@Test
	void should_throw_exception_when_a_non_student_wants_to_take_a_student_loan() {
		LoanOrderService loanOrderService = new LoanOrderService(null, null);

		thenThrownBy(() -> loanOrderService.studentLoanOrder(notAStudent()))
				.hasMessageContaining("Cannot order student loan");
	}

	@Test
	void should_grant_a_student_loan_when_a_student_applies_for_it() {
		Customer customer = aStudent();
		LoanOrderService loanOrderService = new LoanOrderService(mock(PostgresAccessor.class),
				studentPromoReturningMongoDbAccessor());

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(customer);
	
		Assertions.then(loanOrder)
			.isStudentLoan()
			.hasStudentPromotionWithValue(DEFAULT_STUDENT_PROMO_DISCOUNT_VALUE)
			.hasCommisionEqualTo(DEFAULT_STUDENT_PROMO_COMMISSION_VALUE)
			.hasAmountEqualTo(DEFAULT_STUDENT_PROMO_LOAN_AMOUNT);
	}

	private MongoDbAccessor studentPromoReturningMongoDbAccessor() {
		MongoDbAccessor accessor = mock(MongoDbAccessor.class);
		given(accessor.getPromotionDiscount(STUDENT_PROMO_DISCOUNT_NAME)).willReturn(new BigDecimal(DEFAULT_STUDENT_PROMO_DISCOUNT_VALUE));
		return accessor;
	}

	private Customer aStudent() {
		Customer willBeAStudent = notAStudent();
		willBeAStudent.student();
		return willBeAStudent;
	}

	private Customer notAStudent() {
		return new Customer(UUID.randomUUID(), new Person("A", "B", LocalDate.now(), GENDER.FEMALE, "1234567890"));
	}

	/**
	 * Używamy AssertJ jako wzorca assert object. Klasę umieszczamy tutaj dla lepszej widoczności problemu.
	 */
	static class LoanOrderAssert extends AbstractAssert<LoanOrderAssert, LoanOrder> {

		LoanOrderAssert(LoanOrder actual) {
			super(actual, LoanOrderAssert.class);
			isNotNull();
		}
		
		LoanOrderAssert isStudentLoan() {
			if (actual.getType() != LoanType.STUDENT) {
				failWithMessage("Loan type must be of type student");
			}
			return this;
		}
		
		LoanOrderAssert hasStudentPromotionWithValue(String value) {
			then(actual.getPromotions()).isNotEmpty();
			then(value).isNotBlank();
			Promotion promotion = actual.getPromotions().get(0);
			if (!STUDENT_PROMO_DISCOUNT_NAME.equals(promotion.getName())) {
				failWithMessage("Promotion name should be <%s> but was <%s>", STUDENT_PROMO_DISCOUNT_NAME, promotion.getName());
			}
			BigDecimal bigDecimalValue = new BigDecimal(value);
			if (!bigDecimalValue.equals(promotion.getDiscount())) {
				failWithMessage("Promotion value should be <%s> but was <%s>", bigDecimalValue, promotion.getDiscount());
			}
			return this;
		}
		
		LoanOrderAssert hasCommisionEqualTo(String commission) {
			then(commission).isNotBlank();
			BigDecimal bigDecimalValue = new BigDecimal(commission);
			if (!bigDecimalValue.equals(actual.getCommission())) {
				failWithMessage("Commission value should be <%s> but was <%s>", bigDecimalValue, actual.getCommission());
			}
			return this;
		}
		
		LoanOrderAssert hasAmountEqualTo(String amount) {
			then(amount).isNotBlank();
			BigDecimal bigDecimalValue = new BigDecimal(amount);
			if (!bigDecimalValue.equals(actual.getAmount())) {
				failWithMessage("Loan amount value should be <%s> but was <%s>", bigDecimalValue, actual.getAmount());
			}
			return this;
		}
	}
	
	/**
	 * Klasa zawierająca metody fabrykujące - tworzące nasze implementacji wzorca
	 * assert object.
	 */
	static class Assertions extends org.assertj.core.api.BDDAssertions {
		
		public static LoanOrderAssert then(LoanOrder loanOrder) {
			return new LoanOrderAssert(loanOrder);
		}
	}
}