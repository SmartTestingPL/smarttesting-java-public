package pl.smarttesting.bik.score;

import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

public interface ScoreEvaluation {

	Score evaluate(Pesel pesel);
}
