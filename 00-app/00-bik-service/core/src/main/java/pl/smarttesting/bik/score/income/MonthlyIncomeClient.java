package pl.smarttesting.bik.score.income;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.cost.RestClient;
import pl.smarttesting.bik.score.domain.Pesel;

class MonthlyIncomeClient {

	private static final Logger log = LoggerFactory.getLogger(MonthlyIncomeClient.class);

	private final RestClient restClient;

	private final String monthlyIncomeServiceUrl;

	MonthlyIncomeClient(RestClient restClient, String monthlyIncomeServiceUrl) {
		this.restClient = restClient;
		this.monthlyIncomeServiceUrl = monthlyIncomeServiceUrl;
	}

	BigDecimal getMonthlyIncome(Pesel pesel) {
		BigDecimal monthlyIncome = new BigDecimal(this.restClient.get(this.monthlyIncomeServiceUrl + "/" + pesel.pesel, String.class));
		log.info("Monthly income for id [{}] is [{}]", pesel, monthlyIncome);
		return monthlyIncome;
	}

}
