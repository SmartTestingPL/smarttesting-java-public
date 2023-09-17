package pl.smarttesting.bik.score.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

public class ScoreAnalyzer {
	private static final Logger log = LoggerFactory.getLogger(ScoreAnalyzer.class);
	
	private final CompositeScoreEvaluation compositeScoreEvaluation;

	private final int threshold;

	private final DistributionSummary distributionSummary;

	public ScoreAnalyzer(CompositeScoreEvaluation compositeScoreEvaluation, MeterRegistry meterRegistry,
			int threshold) {
		this.compositeScoreEvaluation = compositeScoreEvaluation;
		this.threshold = threshold;
		this.distributionSummary = DistributionSummary.builder("score.aggregated")
				.publishPercentiles(0.5, 0.99)
				.publishPercentileHistogram()
				.register(meterRegistry);
	}

	public boolean shouldGrantLoan(Pesel pesel) {
		Score aggregateScore = this.compositeScoreEvaluation.aggregateAllScores(pesel);
		int points = aggregateScore.points;
		distributionSummary.record(points);
		boolean aboveThreshold = points >= this.threshold;
		log.info("For PESEL [{}] we got score [{}]. It's [{}] that it's above or equal to the threshold [{}]", pesel, points, aboveThreshold, this.threshold);
		return aboveThreshold;
	}
}
