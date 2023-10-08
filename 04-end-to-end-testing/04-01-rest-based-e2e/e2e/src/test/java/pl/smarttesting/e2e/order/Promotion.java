package pl.smarttesting.e2e.order;

import java.math.BigDecimal;

/**
 * Reprezentuje promocję dla oferty pożyczek.
 */
public class Promotion {

	String name;

	BigDecimal discount;

	Promotion(String name, BigDecimal discount) {
		this.name = name;
		this.discount = discount;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getDiscount() {
		return discount;
	}
}
