package pl.smarttesting.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Przykład zastosowania klasy bazowej w celu zwiększenia czytelności i umożliwienia reużycia kodu.
 * Przykład testowania stanu.
 */
class LoanOrderTest extends LoanOrderTestBase {

	// Testowanie stanu
	@Test
	void shouldAddManagerPromo() {
		LoanOrder loanOrder = new LoanOrder(LocalDate.now(), aCustomer());
		UUID managerUuid = UUID.randomUUID();

		loanOrder.addManagerDiscount(managerUuid);

		assertThat(loanOrder.getPromotions()).hasSize(1);
		assertThat(loanOrder.getPromotions().get(0).getName())
				.contains(managerUuid.toString());
		assertThat(loanOrder.getPromotions().get(0).getDiscount())
				.isEqualTo(new BigDecimal(50));
	}

}