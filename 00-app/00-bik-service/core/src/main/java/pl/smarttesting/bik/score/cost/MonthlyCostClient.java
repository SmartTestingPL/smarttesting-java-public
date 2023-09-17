package pl.smarttesting.bik.score.cost;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.domain.Pesel;

class MonthlyCostClient {

	private static final Logger log = LoggerFactory.getLogger(MonthlyCostClient.class);

	private final RestClient restClient;

	private final String monthlyCostServiceUrl;

	MonthlyCostClient(RestClient restClient, String monthlyCostServiceUrl) {
		this.restClient = restClient;
		this.monthlyCostServiceUrl = monthlyCostServiceUrl;
	}

	BigDecimal getMonthlyCosts(Pesel pesel) {
		String monthlyCostString = this.restClient.get(this.monthlyCostServiceUrl + "/" + pesel.pesel, String.class);
		log.info("Monthly cost for id [{}] is [{}]", pesel, monthlyCostString);
		return new BigDecimal(monthlyCostString);
	}

}
