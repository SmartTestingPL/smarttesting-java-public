package pl.smarttesting.loanorders.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pl.smarttesting.loanorders.customer.Customer;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Reprezentuje wniosek o udzielenie pożyczki.
 */
@Document
public class LoanOrder {

	private String id;

	private UUID uuid = UUID.randomUUID();

	private LocalDate orderDate = LocalDate.now();

	private Customer customer;

	private BigDecimal amount = new BigDecimal("1000");

	private BigDecimal interestRate = new BigDecimal("0.05");

	private BigDecimal commission = new BigDecimal("100");

	private List<Promotion> promotions = new ArrayList<>();

	private Status status = Status.NEW;

	// available for Jackson
	public LoanOrder() {

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

	public BigDecimal getAmount() {
		return amount;
	}

	void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public List<Promotion> getPromotions() {
		return promotions;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public Status getStatus() {
		return status;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	LoanOrder setId(String id) {
		this.id = id;
		return this;
	}

	// visible for Mongo integration
	void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	// visible for Mongo integration
	void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	// visible for Mongo integration
	void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	public UUID getUuid() {
		return uuid;
	}

	public enum Status {
		NEW, VERIFIED, APPROVED, REJECTED
	}
}
