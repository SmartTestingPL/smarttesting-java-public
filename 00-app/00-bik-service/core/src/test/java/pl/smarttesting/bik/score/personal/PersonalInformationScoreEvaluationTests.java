package pl.smarttesting.bik.score.personal;

import java.util.Map;

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
class PersonalInformationScoreEvaluationTests {

	private final PersonalInformationClient client = mock(PersonalInformationClient.class);
	private final PersonalInformationScoreEvaluation scoreEvaluation = new PersonalInformationScoreEvaluation(client, new TestOccupationRepository());

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenNullPersonalInformation() {
		when(client.getPersonalInformation(any(Pesel.class)))
			.thenReturn(null);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	// Test nie przechodzi; obsługa nulli nie została zaimplementowana
	@Disabled
	@Test
	void shouldReturnZeroWhenNullValues() {
		PersonalInformation personalInformation = new PersonalInformation(null, 0, null);
		when(client.getPersonalInformation(any(Pesel.class)))
			.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score).isEqualTo(Score.ZERO);
	}

	//	Test nie przechodzi - obrakuje implementacji dla > 30 lat doświadczenia
	@Disabled
	@ParameterizedTest(name = "The score for {0} number of dependants should be equal to {1}")
	@CsvFileSource(resources = "/work-experience.csv", numLinesToSkip = 1)
	void shouldCalculateScoreBasedOnYearsOfExperience(int yearsOfWorkExperience, int points) {
		PersonalInformation personalInformation = new PersonalInformation(PersonalInformation.Education.NONE, yearsOfWorkExperience, PersonalInformation.Occupation.OTHER);
		when(client.getPersonalInformation(any(Pesel.class)))
				.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(points);
	}

	@Test
	void shouldCalculateScoreWhenForOccupationPresentInRepository() {
		PersonalInformation personalInformation = new PersonalInformation(PersonalInformation.Education.NONE, 0, PersonalInformation.Occupation.PROGRAMMER);
		when(client.getPersonalInformation(any(Pesel.class)))
				.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(30);
	}

	@Test
	void shouldUseZeroPointsDefaultWhenForOccupationNotInRepositoryAndNoEducation() {
		PersonalInformation personalInformation = new PersonalInformation(PersonalInformation.Education.NONE, 0, PersonalInformation.Occupation.DOCTOR);
		when(client.getPersonalInformation(any(Pesel.class)))
				.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(0);
	}

	@Test
	void shouldCalculateScoreForBasicEducation() {
		PersonalInformation personalInformation = new PersonalInformation(PersonalInformation.Education.BASIC, 0, PersonalInformation.Occupation.DOCTOR);
		when(client.getPersonalInformation(any(Pesel.class)))
				.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(10);
	}

	@Test
	void shouldCalculateScoreForMediumEducation() {
		PersonalInformation personalInformation = new PersonalInformation(PersonalInformation.Education.MEDIUM, 0, PersonalInformation.Occupation.DOCTOR);
		when(client.getPersonalInformation(any(Pesel.class)))
				.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(30);
	}

	@Test
	void shouldCalculateScoreForHighEducation() {
		PersonalInformation personalInformation = new PersonalInformation(PersonalInformation.Education.HIGH, 0, PersonalInformation.Occupation.DOCTOR);
		when(client.getPersonalInformation(any(Pesel.class)))
				.thenReturn(personalInformation);

		Score score = scoreEvaluation.evaluate(anId());

		assertThat(score.getPoints()).isEqualTo(50);
	}

}

// Test double dla OccupationRepository; dodaliśmy tylko jeden element do mapy, bo na potrzeby tych testów,
// interesują nas tak na prawdę tutaj tylko 2 sytuacje: 1) dany zawód jest w repozytorium, 2) danego zawodu nie ma w repozytorium
class TestOccupationRepository implements OccupationRepository {

	@Override
	public Map<PersonalInformation.Occupation, Score> getOccupationScores() {
		return Map.of(PersonalInformation.Occupation.PROGRAMMER, new Score(30));
	}
}
