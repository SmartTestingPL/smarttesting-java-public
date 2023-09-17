package pl.smarttesting.bik.score.personal;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;
import pl.smarttesting.bik.score.personal.PersonalInformation.Education;
import pl.smarttesting.bik.score.personal.PersonalInformation.Occupation;

/**
 * Na potrzeby pracy domowej, załóżmy, że ta klasa wykonuje skomplikowane i potencjalnie długotrwałe obliczenia.
 */
class PersonalInformationScoreEvaluation implements ScoreEvaluation {
	
	private static final Logger LOG = LoggerFactory.getLogger(PersonalInformationScoreEvaluation.class);

	private final PersonalInformationClient client;

	private final OccupationRepository occupationRepository;

	public PersonalInformationScoreEvaluation(PersonalInformationClient client, OccupationRepository occupationRepository) {
		this.client = client;
		this.occupationRepository = occupationRepository;
	}

	@Override
	public Score evaluate(Pesel pesel) {
		LOG.info("Evaluating personal info score for [{}]", pesel);
		PersonalInformation personalInformation = client.getPersonalInformation(pesel);
		return Score.ZERO
				.add(scoreForOccupation(personalInformation.occupation))
				.add(scoreForEducation(personalInformation.education))
				.add(scoreForYearsOfWorkExperience(personalInformation.yearsOfWorkExperience));
	}
	
	private Score scoreForOccupation(Occupation occupation) {
		Map<Occupation, Score> occupationToScore = occupationRepository.getOccupationScores();
		LOG.info("Found following mappings {}", occupationToScore);
		Score score = occupationToScore.get(occupation);
		LOG.info("Found score {} for occupation {}", score, occupation);
		return score != null ? score : Score.ZERO;
	}
	
	private Score scoreForEducation(Education education) {
		return switch (education) {
			case BASIC -> new Score(10);
			case HIGH -> new Score(50);
			case MEDIUM -> new Score(30);
			default -> Score.ZERO;
		};
	}
	
	private Score scoreForYearsOfWorkExperience(int yearsOfWorkExperience) {
		if (yearsOfWorkExperience == 1) {
			return new Score(5);
		} else if (yearsOfWorkExperience >= 2 && yearsOfWorkExperience < 5) {
			return new Score(10);
		} else if (yearsOfWorkExperience >= 5 && yearsOfWorkExperience < 10) {
			return new Score(20);
		} else if (yearsOfWorkExperience >= 10 && yearsOfWorkExperience < 15) {
			return new Score(30);
		} else if (yearsOfWorkExperience >= 15 && yearsOfWorkExperience < 20) {
			return new Score(40);
		} else if (yearsOfWorkExperience >= 20 && yearsOfWorkExperience < 30) {
			return new Score(50);
		}
		return Score.ZERO;
	}

}
