package pl.smarttesting.customer;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Klient. Klasa opakowująca osobę do zweryfikowania.
 */
public class Customer {

	private final UUID uuid;
	private final Person person;

	@JsonCreator
	public Customer(@JsonProperty("uuid") UUID uuid, @JsonProperty("person") Person person) {
		this.uuid = uuid;
		this.person = person;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Person getPerson() {
		return person;
	}

	public boolean isStudent() {
		return person.isStudent();
	}

	public void student() {
		person.student();
	}

	@Override
	public String toString() {
		return "Customer{" +
				"uuid=" + uuid +
				", person=" + person +
				'}';
	}
}