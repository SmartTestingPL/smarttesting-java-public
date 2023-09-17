package pl.smarttesting.bik.score.cost;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

class MonthlyCostScoreEvaluation implements ScoreEvaluation {

	private static final Logger log = LoggerFactory.getLogger(MonthlyCostScoreEvaluation.class);
	
	private final MonthlyCostClient client;

	public MonthlyCostScoreEvaluation(MonthlyCostClient client) {
		this.client = client;
	}

	@Override
	public Score evaluate(Pesel pesel) {
		log.info("Evaluating monthly cost score for [{}]", pesel);
		BigDecimal monthlyCosts = this.client.getMonthlyCosts(pesel);
		if (between(monthlyCosts, "0", "500")) {
			return new Score(50);
		} else if (between(monthlyCosts, "501", "1500")) {
			return new Score(40);
		} else if (between(monthlyCosts, "1501", "3500")) {
			return new Score(30);
		} else if (between(monthlyCosts, "5501", "10000")) {
			return new Score(10);
		}
		return Score.ZERO;
	}

	private boolean between(BigDecimal income, String min, String max) {
		return income.compareTo(new BigDecimal(min)) >= 0 && income.compareTo(new BigDecimal(max)) < 0;
	}
}
