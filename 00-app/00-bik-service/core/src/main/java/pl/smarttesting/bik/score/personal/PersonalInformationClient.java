package pl.smarttesting.bik.score.personal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.bik.score.cost.RestClient;
import pl.smarttesting.bik.score.domain.Pesel;

class PersonalInformationClient {

	private static final Logger log = LoggerFactory.getLogger(PersonalInformationClient.class);

	private final RestClient restClient;

	private final String personalInformationServiceUrl;

	PersonalInformationClient(RestClient restClient, String monthlyIncomeServiceUrl) {
		this.restClient = restClient;
		this.personalInformationServiceUrl = monthlyIncomeServiceUrl;
	}

	PersonalInformation getPersonalInformation(Pesel pesel) {
		PersonalInformation personalInformation = this.restClient.get(this.personalInformationServiceUrl + "/" + pesel.pesel, PersonalInformation.class);
		log.info("Personal information for id [{}] is [{}]", pesel, personalInformation);
		return personalInformation;
	}

}
