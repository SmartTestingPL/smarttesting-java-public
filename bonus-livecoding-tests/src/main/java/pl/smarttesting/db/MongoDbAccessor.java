package pl.smarttesting.db;

import java.math.BigDecimal;

/**
 * Interfejs służący do komunikacji z dokumentową bazą danych.
 * Posłuży nam do przykładów zastosowania stubów.
 */
public interface MongoDbAccessor {

	BigDecimal getPromotionDiscount(String promotionName);

	BigDecimal getMinCommission();

}
