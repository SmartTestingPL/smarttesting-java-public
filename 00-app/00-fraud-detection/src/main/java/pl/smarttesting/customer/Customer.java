package pl.smarttesting.customer;

import java.util.UUID;

/**
 * Klient. Klasa opakowująca osobę do zweryfikowania.
 */
public class Customer {

	private UUID uuid;
	private Person person;

	public Customer() {
	}

	public Customer(UUID uuid, Person person) {
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

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public String toString() {
		return "Customer [uuid=" + uuid + ", person=" + person + "]";
	}
	
}