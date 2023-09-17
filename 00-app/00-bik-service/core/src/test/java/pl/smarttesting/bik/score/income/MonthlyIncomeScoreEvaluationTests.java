package pl.smarttesting.bik.score.income;

import java.math.BigDecimal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.smarttesting.bik.score.utils.TestUtils.anId;

/**
 * @author Olga Maciaszek-Sharma
 */
class MonthlyIncomeScoreEvaluationTests {

	private final MonthlyIncomeClient client = mock(MonthlyIncomeClient.class);
	private final MonthlyIncomeScoreEvaluation scoreEvaluation = new MonthlyIncomeScoreEvaluation(client);

	// Test nie przechodzi - obsługa minusowej wartości dochodów nie została dodana; Brakuje implementacji dla przedziału 3501 - 5500 -> 30
	@Disabled
	@ParameterizedTest(name = "The score for monthly income equal {0} should be {1}.")
	@CsvFileSource(resources = "/monthly-income.csv", numLinesToSkip = 1)
	void shouldCalculateScoreBasedOnMonthlyIncome(String monthlyIncome, int points) {
		when(client.getMonthlyIncome(any(Pesel.class)))
			.thenReturn(new BigDecimal(monthlyIncome));

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenMonthlyIncomeNull() {
		when(client.getMonthlyIncome(any(Pesel.class))).thenReturn(null);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

}
