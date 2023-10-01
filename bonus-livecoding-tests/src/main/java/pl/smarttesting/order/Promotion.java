package pl.smarttesting.order;

import java.math.BigDecimal;

/**
 * Reprezentuje promocję dla oferty pożyczek.
 */
public record Promotion(String name, BigDecimal discount) {

}
