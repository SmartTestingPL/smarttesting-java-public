package pl.smarttesting.verifier.customer.verification;

import pl.smarttesting.customer.Person;

/**
 * Klasa udająca klasę, która robi zdecydowanie za dużo.
 */
public class VerifierManagerImpl {

	public boolean verifyTaxInformation(Person person) {
		return true;
	}

	public boolean verifyAddress(Person person) {
		return true;
	}
	
	public boolean verifyName(Person person) {
		return true;
	}
	
	public boolean verifySurname(Person person) {
		return true;
	}
	
	public boolean verifyPhone(Person person) {
		return true;
	}

}
