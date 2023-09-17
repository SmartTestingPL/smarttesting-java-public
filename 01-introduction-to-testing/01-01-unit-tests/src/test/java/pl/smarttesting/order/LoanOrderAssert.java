package pl.smarttesting.order;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Przykład zastosowania wzorca AssertObject.
 */
class LoanOrderAssert {

	private final LoanOrder loanOrder;

	LoanOrderAssert(LoanOrder loanOrder) {
		this.loanOrder = loanOrder;
	}

	static LoanOrderAssert then(LoanOrder loanOrder) {
		return new LoanOrderAssert(loanOrder);
	}

	LoanOrderAssert registeredToday() {
		assertThat(loanOrder.getOrderDate()).isEqualTo(LocalDate.now());
		return this;
	}

	LoanOrderAssert hasPromotion(String promotionName) {
		assertThat(loanOrder.getPromotions())
				.filteredOn(promotion -> promotion.getName().equals(promotionName))
				.size().isEqualTo(1);
		return this;
	}

	LoanOrderAssert hasOnlyOnePromotion() {
		hasPromotionNumber(1);
		return this;
	}

	LoanOrderAssert hasPromotionNumber(int number) {
		assertThat(loanOrder.getPromotions().size()).isEqualTo(number);
		return this;
	}

	LoanOrderAssert firstPromotionHasDiscountValue(BigDecimal number) {
		assertThat(loanOrder.getPromotions().get(0).getDiscount()).isEqualTo(number);
		return this;
	}

	LoanOrderAssert correctStudentLoanOrder() {
		return registeredToday()
				.hasPromotion("Student Promo")
				.hasOnlyOnePromotion()
				.firstPromotionHasDiscountValue(new BigDecimal(10));
	}

}
