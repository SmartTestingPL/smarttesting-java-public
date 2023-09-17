package pl.smarttesting.bik.score.analysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

// Dotyczy lekcji 08-02
class ParallelCompositeScoreEvaluationTests {

	ScoreUpdater scoreUpdater = mock(ScoreUpdater.class);

	@Test
	void should_calculate_scores() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		try {
			ParallelCompositeScoreEvaluation evaluation = new ParallelCompositeScoreEvaluation(Arrays.asList(new TenScoreEvaluation(), new TwentyScoreEvaluation()), executorService, scoreUpdater);

			Score score = evaluation.aggregateAllScores(new Pesel("12345678901"));

			then(score.points).isEqualTo(30);
			BDDMockito.then(scoreUpdater).should().scoreCalculated(BDDMockito.argThat(event -> event.getScore().points == 30 && event.getPesel().pesel.equals("12345678901")));
		} finally {
			executorService.shutdown();
		}
	}

	// Test nie przechodzi; obsługa błędów nie została zaimplementowana
	@Test
	@Disabled("Test wykryje błąd")
	void should_return_0_score_when_exception_thrown() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		try {
			ParallelCompositeScoreEvaluation evaluation = new ParallelCompositeScoreEvaluation(Collections.singletonList(new ExceptionScoreEvaluation()), executorService, scoreUpdater);

			Score score = evaluation.aggregateAllScores(new Pesel("12345678901"));

			then(score).isEqualTo(Score.ZERO);
			BDDMockito.then(scoreUpdater).should().scoreCalculated(BDDMockito.argThat(event -> Score.ZERO.equals(event.getScore()) && event.getPesel().pesel.equals("12345678901")));
		} finally {
			executorService.shutdown();
		}
	}

	static class TenScoreEvaluation implements ScoreEvaluation {

		private static final Logger log = LoggerFactory.getLogger(TenScoreEvaluation.class);

		@Override
		public Score evaluate(Pesel pesel) {
			log.info("Hello from 10");
			return new Score(10);
		}
	}

	static class TwentyScoreEvaluation implements ScoreEvaluation {

		private static final Logger log = LoggerFactory.getLogger(TwentyScoreEvaluation.class);

		@Override
		public Score evaluate(Pesel pesel) {
			log.info("Hello from 20");
			return new Score(20);
		}
	}

	static class ExceptionScoreEvaluation implements ScoreEvaluation {

		private static final Logger log = LoggerFactory.getLogger(ExceptionScoreEvaluation.class);

		@Override
		public Score evaluate(Pesel pesel) {
			log.info("Hello from exception");
			throw new IllegalStateException("Boom!");
		}
	}
}
