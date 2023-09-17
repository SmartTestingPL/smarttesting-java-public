package pl.smarttesting.bik.score.income;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

class MonthlyIncomeScoreEvaluation implements ScoreEvaluation {
	
	private static final Logger LOG = LoggerFactory.getLogger(MonthlyIncomeScoreEvaluation.class);

	private final MonthlyIncomeClient client;

	public MonthlyIncomeScoreEvaluation(MonthlyIncomeClient client) {
		this.client = client;
	}

	@Override
	public Score evaluate(Pesel pesel) {
		LOG.info("Evaluating monthly income score for [{}]", pesel);
		BigDecimal monthlyIncome = client.getMonthlyIncome(pesel);
		if (between(monthlyIncome, "0", "500")) {
			return Score.ZERO;
		} else if (between(monthlyIncome, "501", "1500")) {
			return new Score(10);
		} else if (between(monthlyIncome, "1501", "3500")) {
			return new Score(20);
		} else if (between(monthlyIncome, "5501", "10000")) {
			return new Score(40);
		}
		return new Score(50);
	}

	private boolean between(BigDecimal income, String min, String max) {
		return income.compareTo(new BigDecimal(min)) >= 0 && income.compareTo(new BigDecimal(max)) <= 0;
	}
}
