package pl.smarttesting.order;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.loan.LoanType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Reprezentuje wniosek o udzielenie pożyczki.
 */
public class LoanOrder {

	private final LocalDate orderDate;

	private Customer customer;

	private LoanType type;

	private final BigDecimal amount;

	private final BigDecimal interestRate;

	private final BigDecimal commission;

	private final List<Promotion> promotions = new ArrayList<>();

	public LoanOrder(LocalDate orderDate, Customer customer, BigDecimal amount, BigDecimal interestRate, BigDecimal commission) {
		this.orderDate = orderDate;
		this.customer = customer;
		this.amount = amount;
		this.interestRate = interestRate;
		this.commission = commission;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public List<Promotion> getPromotions() {
		return promotions;
	}

	LocalDate getOrderDate() {
		return orderDate;
	}


}
