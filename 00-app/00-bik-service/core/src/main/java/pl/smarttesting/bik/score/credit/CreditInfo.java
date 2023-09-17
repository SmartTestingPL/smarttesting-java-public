package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;
import java.util.Objects;

class CreditInfo {

	/*
	 * Aktualne zadłużenie (spłacane kredyty, pożyczki, ale także posiadane karty kredytowe czy limity w rachunku, ze szczególnym uwzględnieniem wysokości raty innych kredytów)
	 */
	public BigDecimal currentDebt;

	/*
	 * Koszty utrzymania kredytobiorcy i jego rodziny;
	 */
	public BigDecimal currentLivingCosts;

	/*
	 * Historia kredytowa (sposób, w jaki kredytobiorca spłacał dotychczasowe zobowiązania);
	 */
	public DebtPaymentHistory debtPaymentHistory;

	public CreditInfo(BigDecimal currentDebt, BigDecimal currentLivingCosts, DebtPaymentHistory debtPaymentHistory) {
		this.currentDebt = currentDebt;
		this.currentLivingCosts = currentLivingCosts;
		this.debtPaymentHistory = debtPaymentHistory;
	}

	public CreditInfo() {
	}

	public BigDecimal getCurrentDebt() {
		return currentDebt;
	}

	public void setCurrentDebt(BigDecimal currentDebt) {
		this.currentDebt = currentDebt;
	}

	public BigDecimal getCurrentLivingCosts() {
		return currentLivingCosts;
	}

	public void setCurrentLivingCosts(BigDecimal currentLivingCosts) {
		this.currentLivingCosts = currentLivingCosts;
	}

	public DebtPaymentHistory getDebtPaymentHistory() {
		return debtPaymentHistory;
	}

	public void setDebtPaymentHistory(DebtPaymentHistory debtPaymentHistory) {
		this.debtPaymentHistory = debtPaymentHistory;
	}

	public enum DebtPaymentHistory {
		NOT_A_SINGLE_PAID_INSTALLMENT,

		MULTIPLE_UNPAID_INSTALLMENTS,

		INDIVIDUAL_UNPAID_INSTALLMENTS,

		NOT_A_SINGLE_UNPAID_INSTALLMENT
	}

	@Override
	public String toString() {
		return "CreditInfo [currentDebt=" + currentDebt + ", currentLivingCosts=" + currentLivingCosts
				+ ", debtPaymentHistory=" + debtPaymentHistory + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CreditInfo that = (CreditInfo) o;
		return Objects.equals(currentDebt, that.currentDebt) && Objects.equals(currentLivingCosts, that.currentLivingCosts) && debtPaymentHistory == that.debtPaymentHistory;
	}

	@Override
	public int hashCode() {
		return Objects.hash(currentDebt, currentLivingCosts, debtPaymentHistory);
	}
}
