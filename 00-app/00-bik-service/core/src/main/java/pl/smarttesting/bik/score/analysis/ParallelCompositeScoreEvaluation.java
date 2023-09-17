package pl.smarttesting.bik.score.analysis;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;
import pl.smarttesting.bik.score.domain.ScoreCalculatedEvent;

public class ParallelCompositeScoreEvaluation implements CompositeScoreEvaluation {
	
	private static final Logger log = LoggerFactory.getLogger(ParallelCompositeScoreEvaluation.class);
	
	private final List<ScoreEvaluation> scoreEvaluations;

	private final ExecutorService executorService;
	
	private final ScoreUpdater scoreUpdater;

	public ParallelCompositeScoreEvaluation(List<ScoreEvaluation> scoreEvaluations, 
			ExecutorService executorService, ScoreUpdater scoreUpdater) {
		this.scoreEvaluations = scoreEvaluations;
		this.executorService = executorService;
		this.scoreUpdater = scoreUpdater; 
	}

	@Override
	public Score aggregateAllScores(Pesel pesel) {
		Score score = this.scoreEvaluations.stream()
				.map(se -> executorService.submit(() -> se.evaluate(pesel)))
				.map(this::getScore)
				.reduce(Score.ZERO, Score::add);
		log.info("Calculated score {} for pesel {}", score, pesel);
		this.scoreUpdater.scoreCalculated(new ScoreCalculatedEvent(pesel, score));
		return score;
	}

	private Score getScore(Future<Score> sf) {
		try {
			return sf.get(1, TimeUnit.MINUTES);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
