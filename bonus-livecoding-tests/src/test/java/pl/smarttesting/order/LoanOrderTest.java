package pl.smarttesting.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Przykład zastosowania klasy bazowej w celu zwiększenia czytelności i umożliwienia reużycia kodu.
 * Przykład testowania stanu.
 */
class LoanOrderTest extends LoanOrderTestBase {

	// Testowanie stanu
	@Test
	void shouldAddManagerPromo() {
		LoanOrder loanOrder = new LoanOrder(LocalDate.now(), aCustomer(), new BigDecimal(2000), new BigDecimal(5), new BigDecimal(200));
		UUID managerUuid = UUID.randomUUID();

		loanOrder.addManagerDiscount(managerUuid);

		assertThat(loanOrder.getPromotions()).hasSize(1);
		assertThat(loanOrder.getPromotions().get(0).name())
				.contains(managerUuid.toString());
		assertThat(loanOrder.getPromotions().get(0).discount())
				.isEqualTo(new BigDecimal(50));
	}

}