package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

class CreditInfoScoreEvaluation implements ScoreEvaluation {
	
	private static final Logger log = LoggerFactory.getLogger(CreditInfoScoreEvaluation.class);

	private final CreditInfoRepository creditInfoRepository;

	public CreditInfoScoreEvaluation(CreditInfoRepository creditInfoRepository) {
		this.creditInfoRepository = creditInfoRepository;
	}

	@Override
	public Score evaluate(Pesel pesel) {
		log.info("Evaluating credit info score for {}", pesel);
		CreditInfo creditInfo = this.creditInfoRepository.findCreditInfo(pesel);
		if (creditInfo == null) {
			return Score.ZERO;
		}
		return Score.ZERO
				.add(scoreForCurrentDebt(creditInfo.currentDebt))
				.add(scoreForCurrentLivingCosts(creditInfo.currentLivingCosts))
				.add(scoreForDebtPaymentHistory(creditInfo.debtPaymentHistory));
	}

	private Score scoreForCurrentDebt(BigDecimal currentDebt) {
		if (between(currentDebt, "5501", "10000")) {
			return Score.ZERO;
		} else if (between(currentDebt, "3501", "5500")) {
			return new Score(10);
		} else if (between(currentDebt, "1501", "3500")) {
			return new Score(20);
		} else if (between(currentDebt, "500", "1500")) {
			return new Score(40);
		}
		return new Score(50);
	}

	private Score scoreForCurrentLivingCosts(BigDecimal currentDebt) {
		if (between(currentDebt, "6501", "10000")) {
			return Score.ZERO;
		} else if (between(currentDebt, "4501", "6500")) {
			return new Score(10);
		} else if (between(currentDebt, "2501", "4500")) {
			return new Score(20);
		} else if (between(currentDebt, "1000", "2500")) {
			return new Score(40);
		}
		return new Score(50);
	}

	private Score scoreForDebtPaymentHistory(CreditInfo.DebtPaymentHistory debtPaymentHistory) {
		return switch (debtPaymentHistory) {
			case MULTIPLE_UNPAID_INSTALLMENTS -> new Score(10);
			case NOT_A_SINGLE_UNPAID_INSTALLMENT -> new Score(50);
			case INDIVIDUAL_UNPAID_INSTALLMENTS -> new Score(30);
			default -> Score.ZERO;
		};
	}

	private boolean between(BigDecimal income, String min, String max) {
		return income.compareTo(new BigDecimal(min)) >= 0 && income.compareTo(new BigDecimal(max)) <= 0;
	}

}
