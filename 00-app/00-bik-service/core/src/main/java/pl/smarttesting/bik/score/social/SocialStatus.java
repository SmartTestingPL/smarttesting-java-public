package pl.smarttesting.bik.score.social;

class SocialStatus {

	// Liczba osób na utrzymaniu
	public int noOfDependants;

	// Liczba osób w gospodarstwie domowym
	public int noOfPeopleInTheHousehold;

	public MaritalStatus maritalStatus;

	public ContractType contractType;

	public SocialStatus(int noOfDependants, int noOfPeopleInTheHousehold, MaritalStatus maritalStatus, ContractType contractType) {
		this.noOfDependants = noOfDependants;
		this.noOfPeopleInTheHousehold = noOfPeopleInTheHousehold;
		this.maritalStatus = maritalStatus;
		this.contractType = contractType;
	}

	public SocialStatus() {
	}

	public int getNoOfDependants() {
		return noOfDependants;
	}

	public void setNoOfDependants(int noOfDependants) {
		this.noOfDependants = noOfDependants;
	}

	public int getNoOfPeopleInTheHousehold() {
		return noOfPeopleInTheHousehold;
	}

	public void setNoOfPeopleInTheHousehold(int noOfPeopleInTheHousehold) {
		this.noOfPeopleInTheHousehold = noOfPeopleInTheHousehold;
	}

	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public ContractType getContractType() {
		return contractType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	@Override
	public String toString() {
		return "SocialStatus [noOfDependants=" + noOfDependants + ", noOfPeopleInTheHousehold="
				+ noOfPeopleInTheHousehold + ", maritalStatus=" + maritalStatus + ", contractType=" + contractType
				+ "]";
	}

	public enum MaritalStatus {
		SINGLE, 
		
		MARRIED
	}

	public enum ContractType {

		/**
		 * UoP.
		 */
		EMPLOYMENT_CONTRACT,

		/**
		 * Własna działalność.
		 */
		OWN_BUSINESS_ACTIVITY,

		UNEMPLOYED;
	}
}
