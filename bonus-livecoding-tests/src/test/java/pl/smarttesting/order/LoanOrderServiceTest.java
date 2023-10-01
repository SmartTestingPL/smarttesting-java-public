package pl.smarttesting.order;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.db.MongoDbAccessor;
import pl.smarttesting.db.PostgresAccessor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static pl.smarttesting.order.LoanOrderAssert.then;

/**
 * Klasa zawiera przykłady różnych sposobów setupu i tear-downu testów
 * i przykłady zastosowania stubów i mocków.
 */
class LoanOrderServiceTest extends LoanOrderTestBase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LoanOrderServiceTest.class);
	// Mock, który będzie wykorzystywany później do weryfikacji interakcji
	PostgresAccessor postgresAccessor = mock(PostgresAccessor.class);

	// Tworzenie obiektów stub/ mock
	// Ten obiekt tak naprawdę jest wyłącznie stubem (nie używamy go do weryfikacji interakcji),
	// a to, że jest tworzony metodą `mock(...)` to wyłącznie specyfika frameworku.
	// Np. w Spocku użylibyśmy wywołania `Stub(MongoDBAccessor)`
	MongoDbAccessor mongoDbAccessor = mock(MongoDbAccessor.class);
	// Alternatywne sposoby setupu testów: pole
	LoanOrderService loanOrderService = new LoanOrderService(postgresAccessor, mongoDbAccessor);
	Customer student;

	// Metoda setupująca frameworku wywoływana raz przed wywołaniem którejkolwiek metody testowej w klasie
	@BeforeAll
	static void beforeAllTests() {
		LOGGER.info("Running the tests.");
	}

	// Metoda Tear-down wywoływana po zakończeniu wszystkich testów w klasie
	@AfterAll
	static void afterAllTests() {
		LOGGER.info("Finished running the tests.");
	}

	// Alternatywne sposoby setupu testów: metoda setupująca frameworku wywoływana przed każdym testem
	@BeforeEach
	void setUp() {
		student = aStudent();

		// Stubowanie metody getPromotionDiscount(...)
		when(mongoDbAccessor.getPromotionDiscount("Student Promo"))
				.thenReturn(new BigDecimal(10));
	}

	// Testowanie wyniku operacji
	@Test
	void shouldCreateStudentLoanOrder() {
		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		assertThat(loanOrder.getOrderDate()).isEqualTo(LocalDate.now());
		assertThat(loanOrder.getPromotions())
				.filteredOn(promotion -> promotion.name().equals("Student Promo"))
				.size().isEqualTo(1);
		assertThat(loanOrder.getPromotions().size()).isEqualTo(1);
		assertThat(loanOrder.getPromotions().get(0).discount())
				.isEqualTo(new BigDecimal(10));
	}

	@Test
	void shouldUpdatePromotionStatistics() {
		loanOrderService.studentLoanOrder(student);

		// Weryfikacja interakcji z użyciem obiektu, który jest też stosowany jako stub
		verify(postgresAccessor).updatePromotionStatistics("Student Promo");

		// Weryfikacja tego, że dana interakcja nie wystąpiła
		verify(postgresAccessor, never())
				.updatePromotionDiscount(eq("Student Promo"), any());

		// Alternatywna asercja wobec tej powyżej: można zweryfikować, czy nie nastąpiła
		// żadna inna asercja na danym mocku (dla przykładu dodaliśmy obydwie, ale
		// normalnie byłoby to stosowane zamiast tej asercji powyżej)
		verifyNoMoreInteractions(postgresAccessor);
	}

	// Przykład AssertObject Pattern
	@Test
	void assertObjectShouldCreateStudentLoan() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		LoanOrderAssert orderAssert = new LoanOrderAssert(loanOrder);
		orderAssert.registeredToday();
		orderAssert.hasPromotion("Student Promo");
		orderAssert.hasOnlyOnePromotion();
		orderAssert.firstPromotionHasDiscountValue(new BigDecimal(10));
	}

	// Przykład AssertObject Pattern z chainowaniem asercji
	@Test
	void chainedAssertObjectShouldCreateStudentLoanOrder() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		then(loanOrder).registeredToday()
				.hasPromotion("Student Promo")
				.hasOnlyOnePromotion()
				.firstPromotionHasDiscountValue(new BigDecimal(10));
	}

	// Przykład AssertObject Pattern z zastosowaniem metody wrappującej chain asercji
	@Test
	void chainedAssertObjectShouldCreateStudentLoanOrderSimpleAssertion() {
		Customer student = aStudent();

		LoanOrder loanOrder = loanOrderService.studentLoanOrder(student);

		then(loanOrder).correctStudentLoan();
	}

}