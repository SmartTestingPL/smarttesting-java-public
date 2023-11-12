package pl.smarttesting.order;

import java.math.BigDecimal;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(discount, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Promotion other = (Promotion) obj;
		return Objects.equals(discount, other.discount) && Objects.equals(name, other.name);
	}
	
}
