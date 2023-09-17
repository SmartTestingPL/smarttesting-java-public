package pl.smarttesting.bik.score.personal;

class PersonalInformation {

	Education education;

	int yearsOfWorkExperience;

	Occupation occupation;

	PersonalInformation(Education education, int yearsOfWorkExperience, Occupation occupation) {
		this.education = education;
		this.yearsOfWorkExperience = yearsOfWorkExperience;
		this.occupation = occupation;
	}

	PersonalInformation() {
	}

	Education getEducation() {
		return education;
	}

	void setEducation(Education education) {
		this.education = education;
	}

	int getYearsOfWorkExperience() {
		return yearsOfWorkExperience;
	}

	void setYearsOfWorkExperience(int yearsOfWorkExperience) {
		this.yearsOfWorkExperience = yearsOfWorkExperience;
	}

	Occupation getOccupation() {
		return occupation;
	}

	void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}

	enum Education {
		NONE,

		BASIC,

		MEDIUM,

		HIGH
	}

	// lol
	enum Occupation {
		PROGRAMMER,

		LAWYER,

		DOCTOR,

		OTHER
	}

	@Override
	public String toString() {
		return "PersonalInformation [education=" + education + ", yearsOfWorkExperience=" + yearsOfWorkExperience
				+ ", occupation=" + occupation + "]";
	}
	

}
