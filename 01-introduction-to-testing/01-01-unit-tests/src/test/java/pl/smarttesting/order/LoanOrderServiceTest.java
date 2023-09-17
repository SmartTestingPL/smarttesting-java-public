package pl.smarttesting.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static pl.smarttesting.order.LoanOrderAssert.then;

class LoanOrderServiceTest {

	LoanOrderService loanOrderService = new LoanOrderService();

	// Wywołanie metody setupującej w teście.
	@Test
	void shouldCreateStudentLoanOrder() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		assertThat(loanOrder.getOrderDate()).isEqualTo(LocalDate.now());
		assertThat(loanOrder.getPromotions())
				.filteredOn(promotion -> promotion.getName().equals("Student Promo"))
				.size().isEqualTo(1);
		assertThat(loanOrder.getPromotions().size()).isEqualTo(1);
		assertThat(loanOrder.getPromotions().get(0).getDiscount())
				.isEqualTo(new BigDecimal(10));
	}

	// Przykład zastosowania AssertObject Pattern
	@Test
	void assertObjectShouldCreateStudentLoanOrder() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		LoanOrderAssert orderAssert = new LoanOrderAssert(loanOrder);
		orderAssert.registeredToday();
		orderAssert.hasPromotion("Student Promo");
		orderAssert.hasOnlyOnePromotion();
		orderAssert.firstPromotionHasDiscountValue(new BigDecimal(10));
	}

	// Przykład zastosowania AssertObject Pattern z chainowaniem asercji
	@Test
	void chainedAssertObjectShouldCreateStudentLoanOrder() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		then(loanOrder).registeredToday()
				.hasPromotion("Student Promo")
				.hasOnlyOnePromotion()
				.firstPromotionHasDiscountValue(new BigDecimal(10));
	}

	// Przykład zastosowania AssertObject Pattern z użyciem metody wrappującej chain asercji
	@Test
	void chainedAssertObjectShouldCreateStudentLoanOrderSimpleAssertion() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		then(loanOrder).correctStudentLoanOrder();
	}

	// Metoda zawierająca setup klientów typu STUDENT na potrzeby testów
	private Customer aStudent() {
		Customer customer = new Customer(UUID
				.randomUUID(), new Person("John", "Smith",
				LocalDate.of(1996, 8, 28), Person.GENDER.MALE,
				"96082812079"));
		customer.student();
		return customer;
	}

}