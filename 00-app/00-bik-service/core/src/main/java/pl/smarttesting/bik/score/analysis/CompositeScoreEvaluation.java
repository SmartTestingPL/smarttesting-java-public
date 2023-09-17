package pl.smarttesting.bik.score.analysis;

import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

public interface CompositeScoreEvaluation {
	
	Score aggregateAllScores(Pesel pesel);
}
