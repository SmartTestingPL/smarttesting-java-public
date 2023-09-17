package pl.smarttesting.bik.score.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.cost.RestClient;
import pl.smarttesting.bik.score.domain.Pesel;

class SocialStatusClient {

	private static final Logger log = LoggerFactory.getLogger(SocialStatusClient.class);

	private final RestClient restClient;

	private final String socialStatusServiceUrl;

	SocialStatusClient(RestClient restClient, String monthlyIncomeServiceUrl) {
		this.restClient = restClient;
		this.socialStatusServiceUrl = monthlyIncomeServiceUrl;
	}

	SocialStatus getSocialStatus(Pesel pesel) {
		SocialStatus socialStatus = this.restClient.get(this.socialStatusServiceUrl + "/" + pesel.pesel, SocialStatus.class);
		log.info("Social status for id [{}] is [{}]", pesel, socialStatus);
		return socialStatus;
	}

}
