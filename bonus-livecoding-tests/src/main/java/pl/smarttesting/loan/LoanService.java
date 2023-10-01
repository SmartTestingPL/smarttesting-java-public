package pl.smarttesting.loan;

import pl.smarttesting.db.MongoDbAccessor;
import pl.smarttesting.db.PostgresAccessor;
import pl.smarttesting.event.EventEmitter;
import pl.smarttesting.loan.validation.CommissionValidationException;
import pl.smarttesting.loan.validation.NumberOfInstallmentsValidationException;
import pl.smarttesting.order.LoanOrder;
import pl.smarttesting.order.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LoanService {

	private final EventEmitter eventEmitter;
	private final MongoDbAccessor mongoDbAccessor;
	private final PostgresAccessor postgresAccessor;

	public LoanService(EventEmitter eventEmitter, MongoDbAccessor mongoDbAccessor,
					   PostgresAccessor postgresAccessor) {
		this.eventEmitter = eventEmitter;
		this.mongoDbAccessor = mongoDbAccessor;
		this.postgresAccessor = postgresAccessor;
	}

	Loan createLoan(LoanOrder loanOrder, int numberOfInstallments) {
		// Forget to pass argument (validate field instead)
		validateNumberOfInstallments(numberOfInstallments);
		// Forget to add this method add first
		validateCommission(loanOrder.getCommission());
		updatePromotions(loanOrder);
		Loan loan = new Loan(LocalDate.now(), loanOrder, numberOfInstallments);
		eventEmitter.emit(new LoanCreatedEvent(loan.getUuid()));
		return loan;
	}

	private void validateCommission(BigDecimal commission) {
		if (commission == null || commission.compareTo(mongoDbAccessor.getMinCommission()) <= 0) {
			throw new CommissionValidationException();
		}
	}

	private void validateNumberOfInstallments(int numberOfInstallments) {
		if (numberOfInstallments <= 0) {
			throw new NumberOfInstallmentsValidationException();
		}
	}

	// Visible for tests
	// Potencjalny kandydat na osobną klasę
	// TODO: private
	void updatePromotions(LoanOrder loanOrder) {
		List<Promotion> updatedPromotions = loanOrder.getPromotions().stream()
				.filter(promotion -> postgresAccessor.getValidPromotionsForDate(LocalDate.now()).contains(promotion))
				.collect(Collectors.toList());
		loanOrder.getPromotions().clear();
		loanOrder.getPromotions().addAll(updatedPromotions);
	}
}
