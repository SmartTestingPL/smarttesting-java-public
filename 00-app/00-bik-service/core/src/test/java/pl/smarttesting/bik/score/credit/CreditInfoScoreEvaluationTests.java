package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
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
import static pl.smarttesting.bik.score.credit.CreditInfo.DebtPaymentHistory.NOT_A_SINGLE_PAID_INSTALLMENT;
import static pl.smarttesting.bik.score.utils.TestUtils.anId;

/**
 * @author Olga Maciaszek-Sharma
 */
class CreditInfoScoreEvaluationTests {

	CreditInfoRepository repository;
	CreditInfoScoreEvaluation scoreEvaluation;

	@BeforeEach
	void setUp() {
		repository = mock(CreditInfoRepository.class);
		scoreEvaluation = new CreditInfoScoreEvaluation(repository);
	}

	@Test
	void shouldReturnZeroForNullCreditInfo() {
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(null);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenNullCreditInfoFieldsPresent() {
		CreditInfo creditInfo = new CreditInfo(null, null, null);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	// Test nie przechodzi; przedział powyżej 10000 nie został zaimplementowany;
	// obsługa niepoprawnej wartości -1 nie została zaimplementowana
	@Disabled
	@ParameterizedTest(name = "The score for livingCost equal {0} should be {1}.")
	@CsvFileSource(resources = "/living-cost.csv", numLinesToSkip = 1)
	void shouldEvaluateScoreBasedOnCurrentLivingCost(String livingCost, int points) {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("5501"), new BigDecimal(livingCost),
			NOT_A_SINGLE_PAID_INSTALLMENT);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	// Test nie przechodzi; przedział powyżej 10000 nie został zaimplementowany;
	// obsługa niepoprawnej wartości -1 nie została zaimplementowana
	@Disabled
	@ParameterizedTest(name = "The score for currentDebt equal {0} should be {1}.")
	@CsvFileSource(resources = "/current-debt.csv", numLinesToSkip = 1)
	void shouldEvaluateScoreBasedOnCurrentDebt(String currentDebt, int points) {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal(currentDebt), new BigDecimal("6501"),
			NOT_A_SINGLE_PAID_INSTALLMENT);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	@Test
	void shouldEvaluateScoreForNotPayingCustomer() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("5501"), new BigDecimal("6501"),
			NOT_A_SINGLE_PAID_INSTALLMENT);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(0);
	}

	@Test
	void shouldEvaluateScoreForAlwaysPayingCustomer() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("5501"), new BigDecimal("6501"),
			CreditInfo.DebtPaymentHistory.NOT_A_SINGLE_UNPAID_INSTALLMENT);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(50);
	}

	@Test
	void shouldEvaluateScoreForOftenMissingPaymentCustomer() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("5501"), new BigDecimal("6501"),
			CreditInfo.DebtPaymentHistory.MULTIPLE_UNPAID_INSTALLMENTS);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(10);
	}

	@Test
	void shouldEvaluateScoreForRarelyMissingPaymentCustomer() {
		CreditInfo creditInfo = new CreditInfo(new BigDecimal("5501"), new BigDecimal("6501"),
			CreditInfo.DebtPaymentHistory.INDIVIDUAL_UNPAID_INSTALLMENTS);
		when(repository.findCreditInfo(any(Pesel.class))).thenReturn(creditInfo);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(30);
	}

}
