package pl.smarttesting.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import pl.smarttesting.db.MongoDbAccessor;
import pl.smarttesting.db.PostgresAccessor;
import pl.smarttesting.event.EventEmitter;
import pl.smarttesting.loan.validation.CommissionValidationException;
import pl.smarttesting.order.LoanOrder;
import pl.smarttesting.order.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pl.smarttesting.loan.LoanTestUtils.aLoanOrder;

class LoanServiceTest {

	private EventEmitter eventEmitter = mock(EventEmitter.class);
	private MongoDbAccessor mongoDbAccessor = mock(MongoDbAccessor.class);
	private LoanService loanService = new LoanService(eventEmitter, mongoDbAccessor, new TestPostgresAccessor());
	private ArgumentCaptor<LoanCreatedEvent> loanCreatedEventArgumentCaptor = ArgumentCaptor
			.forClass(LoanCreatedEvent.class);

	@BeforeEach
	void setUp() {
		when(mongoDbAccessor.getMinCommission())
				.thenReturn(new BigDecimal(200));
	}

	@Test
	void shouldCreateLoan() {
		LoanOrder loanOrder = aLoanOrder();

		Loan loan = loanService.createLoan(loanOrder, 3);

		assertThat(loan.getUuid()).isNotNull();
	}

	@Test
	void shouldEmitEventWhenLoanCreated() {
		LoanOrder loanOrder = aLoanOrder();

		loanService.createLoan(loanOrder, 3);

		verify(eventEmitter).emit(loanCreatedEventArgumentCaptor.capture());
		assertThat(loanCreatedEventArgumentCaptor.getValue().getLoanUuid()).isNotNull();
		// verifyNoInteractions(eventEmitter);
		verifyNoMoreInteractions(eventEmitter);
	}

	@ParameterizedTest(name = "should throw exception for commission = {0}")
	@MethodSource("commissionValues")
	void shouldThrowExceptionWhenIncorrectCommission(BigDecimal commission) {
		assertThatExceptionOfType(CommissionValidationException.class)
				.isThrownBy(() -> loanService.createLoan(aLoanOrder(
						new BigDecimal(2000), new BigDecimal(5), commission), 5));
	}

	@Test
	void shouldNotThrowExceptionIfEmptyPromotions() {
		assertThatCode(() -> loanService.createLoan(aLoanOrder(new Promotion[]{}), 6))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldRemoveIncorrectPromotions() {
		LoanOrder loanOrder = aLoanOrder(new Promotion("promotion not in DB", new BigDecimal(55)));

		loanService.updatePromotions(loanOrder);

		assertThat(loanOrder.getPromotions()).isEmpty();
	}

	private static Stream<BigDecimal> commissionValues() {
		return Stream.of(null, BigDecimal.ZERO, new BigDecimal(-1), new BigDecimal(199));
	}
}

class TestPostgresAccessor implements PostgresAccessor {

	@Override
	public void updatePromotionStatistics(String promotionName) {
		// do nothing
	}

	@Override
	public void updatePromotionDiscount(String promotionName, BigDecimal newDiscount) {
		// do nothing
	}

	@Override
	public List<Promotion> getValidPromotionsForDate(LocalDate localDate) {
		return Arrays.asList(new Promotion("test 10", new BigDecimal(10)),
				new Promotion("test 20", new BigDecimal(20)));
	}
}