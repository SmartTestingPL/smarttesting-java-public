package pl.smarttesting.verifier.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.smarttesting.customer.Person;

/**
 * Obiekt, który wysyłamy poprzez brokera.
 * Reprezentuje osobę i rezultat weryfikacji.
 */
public class CustomerVerification implements Serializable {

	private Person person;

	private CustomerVerificationResult result;

	@JsonCreator
	public CustomerVerification(@JsonProperty("person") Person person, @JsonProperty("result") CustomerVerificationResult result) {
		this.person = person;
		this.result = result;
	}

	public CustomerVerification() {
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public void setResult(CustomerVerificationResult result) {
		this.result = result;
	}

	public Person getPerson() {
		return person;
	}

	public CustomerVerificationResult getResult() {
		return result;
	}

	@Override
	public String toString() {
		return "CustomerVerification{" +
				"person=" + person +
				", result=" + result +
				'}';
	}
}
