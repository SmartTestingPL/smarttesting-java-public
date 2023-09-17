package pl.smarttesting.bik.score.social;

import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;

class SocialStatusScoreEvaluation implements ScoreEvaluation {

	private final SocialStatusClient client;

	public SocialStatusScoreEvaluation(SocialStatusClient client) {
		this.client = client;
	}

	@Override
	public Score evaluate(Pesel pesel) {
		SocialStatus socialStatus = client.getSocialStatus(pesel);
		return Score.ZERO
				.add(scoreForNoOfDependants(socialStatus))
				.add(scoreForNoOfPeopleInTheHousehold(socialStatus))
				.add(scoreForMaritalStatus(socialStatus))
				.add(scoreForContractType(socialStatus));
	}

	private Score scoreForNoOfDependants(SocialStatus socialStatus) {
		if (socialStatus.noOfDependants == 0) {
			return new Score(50);
		}
		if (socialStatus.noOfDependants == 1) {
			return new Score(40);
		}
		else if (socialStatus.noOfDependants == 2) {
			return new Score(30);
		}
		else if (socialStatus.noOfDependants == 3) {
			return new Score(20);
		}
		else if (socialStatus.noOfDependants == 4) {
			return new Score(10);
		}
		return Score.ZERO;
	}


	private Score scoreForNoOfPeopleInTheHousehold(SocialStatus socialStatus) {
		if (socialStatus.noOfPeopleInTheHousehold == 1) {
			return new Score(50);
		}
		else if (socialStatus.noOfPeopleInTheHousehold > 1 && socialStatus.noOfPeopleInTheHousehold <= 2) {
			return new Score(40);
		}
		else if (socialStatus.noOfPeopleInTheHousehold > 2 && socialStatus.noOfPeopleInTheHousehold < 3) {
			return new Score(30);
		}
		else if (socialStatus.noOfPeopleInTheHousehold > 3 && socialStatus.noOfPeopleInTheHousehold <= 4) {
			return new Score(20);
		} else if (socialStatus.noOfPeopleInTheHousehold > 4 && socialStatus.noOfPeopleInTheHousehold <= 5) {
			return new Score(10);
		}
		return Score.ZERO;
	}
	
	private Score scoreForMaritalStatus(SocialStatus socialStatus) {
		return switch (socialStatus.maritalStatus) {
			case SINGLE -> new Score(20);
			case MARRIED -> new Score(10);
		};
	}
	
	private Score scoreForContractType(SocialStatus socialStatus) {
		return switch (socialStatus.contractType) {
			case EMPLOYMENT_CONTRACT -> new Score(20);
			case OWN_BUSINESS_ACTIVITY -> new Score(10);
			default -> Score.ZERO;
		};
	}

}
