package pl.smarttesting.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.loan.LoanType;

/**
 * Reprezentuje wniosek o udzielenie pożyczki.
 */
class LoanOrder {

	private final LocalDate orderDate;

	private Customer customer;

	private LoanType type;

	private BigDecimal amount;

	private BigDecimal interestRate;

	private BigDecimal commission;

	private final List<Promotion> promotions = new ArrayList<>();

	LoanOrder(LocalDate orderDate, Customer customer) {
		this.orderDate = orderDate;
		this.customer = customer;
	}

	void addManagerDiscount(UUID managerId) {
		Promotion promotion = new Promotion("Manager Promo: " + managerId
				.toString(), new BigDecimal(50));
		promotions.add(promotion);
	}

	Customer getCustomer() {
		return customer;
	}

	void setCustomer(Customer customer) {
		this.customer = customer;
	}

	LoanType getType() {
		return type;
	}

	void setType(LoanType type) {
		this.type = type;
	}

	BigDecimal getAmount() {
		return amount;
	}

	void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	BigDecimal getInterestRate() {
		return interestRate;
	}

	void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	BigDecimal getCommission() {
		return commission;
	}

	void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	List<Promotion> getPromotions() {
		return promotions;
	}

	LocalDate getOrderDate() {
		return orderDate;
	}
}
