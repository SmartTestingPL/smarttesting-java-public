package pl.smarttesting.e2e;

import pl.smarttesting.e2e.order.LoanOrder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static pl.smarttesting.e2e.order.LoanOrder.Status.REJECTED;
import static pl.smarttesting.e2e.order.LoanOrder.Status.VERIFIED;

/**
 * Przykład zastosowania wzorca AssertObject.
 */
public class LoanOrderAssert {

	private final LoanOrder loanOrder;

	public LoanOrderAssert(LoanOrder loanOrder) {
		this.loanOrder = loanOrder;
	}

	void customerVerificationPassed() {
		assertThat(loanOrder.getStatus()).isEqualTo(VERIFIED);
	}

	void customerVerificationFailed() {
		assertThat(loanOrder.getStatus()).isEqualTo(REJECTED);
	}

}

