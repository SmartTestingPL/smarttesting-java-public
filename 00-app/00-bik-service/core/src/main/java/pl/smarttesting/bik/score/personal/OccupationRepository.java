package pl.smarttesting.bik.score.personal;

import java.util.Map;

import pl.smarttesting.bik.score.domain.Score;
import pl.smarttesting.bik.score.personal.PersonalInformation.Occupation;

interface OccupationRepository {

	Map<Occupation, Score> getOccupationScores();
}
