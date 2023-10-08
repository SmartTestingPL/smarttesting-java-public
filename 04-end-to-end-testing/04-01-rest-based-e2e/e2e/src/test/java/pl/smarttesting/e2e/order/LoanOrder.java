package pl.smarttesting.e2e.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.smarttesting.e2e.customer.Customer;

/**
 * Reprezentuje wniosek o udzielenie pożyczki.
 */
public class LoanOrder {

	private final UUID uuid = UUID.randomUUID();
	private String id;
	private final LocalDate orderDate = LocalDate.now();

	private Customer customer;

	private BigDecimal amount;

	private BigDecimal interestRate;

	private BigDecimal commission;

	private final List<Promotion> promotions = new ArrayList<>();

	private Status status = Status.NEW;

	public LoanOrder() {

	}

	public LoanOrder(Customer customer) {
		this.customer = customer;
	}

	void addManagerDiscount(UUID managerId) {
		Promotion promotion = new Promotion("Manager Promo: " + managerId
				.toString(), new BigDecimal(50));
		promotions.add(promotion);
	}

	public Customer getCustomer() {
		return customer;
	}

	void setCustomer(Customer customer) {
		this.customer = customer;
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

	public Status getStatus() {
		return status;
	}

	LoanOrder setStatus(Status status) {
		this.status = status;
		return this;
	}

	public enum Status {
		NEW, VERIFIED, APPROVED, REJECTED
	}
}
