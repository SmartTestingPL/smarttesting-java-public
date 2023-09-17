package pl.smarttesting.bik.score.credit;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import pl.smarttesting.bik.score.domain.Pesel;

@Document("creditInfoDocuments")
class CreditInfoDocument {
	
	@Id
	private String id;
	
	private CreditInfo creditInfo;
	
	private Pesel pesel;

	CreditInfoDocument(CreditInfo creditInfo, Pesel pesel) {
		this.creditInfo = creditInfo;
		this.pesel = pesel;
	}

	CreditInfoDocument() {
	}

	CreditInfo getCreditInfo() {
		return creditInfo;
	}

	void setCreditInfo(CreditInfo creditInfo) {
		this.creditInfo = creditInfo;
	}

	Pesel getPesel() {
		return pesel;
	}

	void setPesel(Pesel pesel) {
		this.pesel = pesel;
	}

	@Override
	public String toString() {
		return "CreditInfoDocument [creditInfo=" + creditInfo + ", pesel=" + pesel + "]";
	}
	

}
