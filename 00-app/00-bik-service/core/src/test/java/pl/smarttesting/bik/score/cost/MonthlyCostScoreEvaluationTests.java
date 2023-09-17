package pl.smarttesting.bik.score.cost;

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
class MonthlyCostScoreEvaluationTests {

	private final MonthlyCostClient client = mock(MonthlyCostClient.class);
	private final MonthlyCostScoreEvaluation scoreEvaluation = new MonthlyCostScoreEvaluation(client);

	// Test nie przechodzi; obsługa minusowej wartości kosztów nie została dodana;
	// Brakuje implementacji dla przedziału 3501 - 5500 -> 20; Granice warunków niepoprawnie zaimplementowane
	@Disabled
	@ParameterizedTest(name = "The score for monthly cost equal to {0} should be {1}.")
	@CsvFileSource(resources = "/monthly-cost.csv", numLinesToSkip = 1)
	void shouldCalculateScoreBasedOnMonthlyCost(String monthlyCost, int points) {
		when(client.getMonthlyCosts(any(Pesel.class)))
			.thenReturn(new BigDecimal(monthlyCost));

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenMonthlyScoreNull() {
		when(client.getMonthlyCosts(any(Pesel.class))).thenReturn(null);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

}
