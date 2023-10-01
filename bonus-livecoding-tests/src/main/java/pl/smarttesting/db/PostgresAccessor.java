package pl.smarttesting.db;

import pl.smarttesting.order.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfejs służący do komunikacji z relacyjną bazą danych.
 * Posłuży nam do przykładów zastosowania mocków i weryfikacji interakcji.
 */
public interface PostgresAccessor {

	void updatePromotionStatistics(String promotionName);

	void updatePromotionDiscount(String promotionName, BigDecimal newDiscount);

	List<Promotion> getValidPromotionsForDate(LocalDate localDate);
}
