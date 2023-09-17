package pl.smarttesting.bik.score.social;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;
import pl.smarttesting.bik.score.social.validation.NumberOfHouseholdMembersValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.smarttesting.bik.score.utils.TestUtils.anId;

/**
 * @author Olga Maciaszek-Sharma
 */
class SocialStatusScoreEvaluationTests {

	private final SocialStatusClient client = mock(SocialStatusClient.class);
	private final SocialStatusScoreEvaluation scoreEvaluation = new SocialStatusScoreEvaluation(client);

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenNullSocialStatus() {
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(null);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenNullMaritalStatus() {
		SocialStatus status = new SocialStatus(0, 0, null, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenNullEmploymentContract() {
		SocialStatus status = new SocialStatus(0, 0, SocialStatus.MaritalStatus.MARRIED, null);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	//Test nie przechodzi - logika nie została zaimplementowana
	// Zazwyczaj w tego typu przypadkach testowych chcemy zweryfikować, w zależności od wymagań biznesowych, albo, że jest rzucany odpowiedni wyjątek biznesowy
	// albo, że żaden wyjątek nie jest rzucony i błąd jest odpowiednio obsłużony w algorytmie (np. może być zwrócone Score.ZERO)
	@Disabled
	@ParameterizedTest(name = "Should throw exception when no. of dependents equals {0} and no. of household members equals {1}")
	@CsvSource({"0, 0", "-1, 0", "0, -1", "2, 1", "1, 1"})
	void shouldThrowBusinessExceptionWhenIncorrectNumbersOfMembersAndDependants(int numberOfDependants, int numberOfHouseholdMembers) {
		SocialStatus status = new SocialStatus(numberOfDependants, numberOfHouseholdMembers,
			SocialStatus.MaritalStatus.SINGLE, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		assertThatExceptionOfType(NumberOfHouseholdMembersValidationException.class)
				.isThrownBy(
						() -> scoreEvaluation.evaluate(anId()));
	}

	// Test nie przechodzi: warunki brzegowe dla 3 członków gospodarstwa domowego nie zaimplementowane poprawnie
	@Disabled
	@ParameterizedTest(name = "The score for {0} household members should be equal to {1}")
	@CsvFileSource(resources = "/household-members.csv", numLinesToSkip = 1)
	void shouldCalculateScoreDependingOnNumberOfHouseholdMembers(int numberOfHouseholdMembers, int points) {
		SocialStatus status = new SocialStatus(0, numberOfHouseholdMembers,
				SocialStatus.MaritalStatus.SINGLE, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	@ParameterizedTest(name = "The score for {0} number of dependants should be equal to {1}")
	@CsvFileSource(resources = "/dependants.csv", numLinesToSkip = 1)
	void shouldCalculateScoreDependingOnNumberOfDependants(int numberOfDependants, int points) {
		SocialStatus status = new SocialStatus(numberOfDependants, 6,
				SocialStatus.MaritalStatus.SINGLE, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	@ParameterizedTest(name = "The score for {0} number of dependants should be equal to {1}")
	@CsvFileSource(resources = "/dependants.csv", numLinesToSkip = 1)
	void shouldCalculateScoreDependingOnMaritalStatus(int numberOfDependants, int points) {
		SocialStatus status = new SocialStatus(numberOfDependants, 6,
				SocialStatus.MaritalStatus.SINGLE, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	@Test
	void shouldCalculateScoreWhenCustomerSingle() {
		SocialStatus status = new SocialStatus(0, 6,
				SocialStatus.MaritalStatus.SINGLE, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(90);
	}

	@Test
	void shouldCalculateScoreWhenCustomerMarriedAndEmploymentContract() {
		SocialStatus status = new SocialStatus(0, 6,
				SocialStatus.MaritalStatus.MARRIED, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(80);
	}


	@Test
	void shouldCalculateScoreWhenOwnBusiness() {
		SocialStatus status = new SocialStatus(0, 6,
				SocialStatus.MaritalStatus.MARRIED, SocialStatus.ContractType.OWN_BUSINESS_ACTIVITY);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(70);
	}

	@Test
	void shouldCalculateScoreWhenUnemployed() {
		SocialStatus status = new SocialStatus(0, 6,
				SocialStatus.MaritalStatus.MARRIED, SocialStatus.ContractType.UNEMPLOYED);
		when(client.getSocialStatus(any(Pesel.class))).thenReturn(status);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(60);
	}

}
