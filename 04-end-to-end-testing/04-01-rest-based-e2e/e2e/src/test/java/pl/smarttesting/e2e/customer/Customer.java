package pl.smarttesting.e2e.customer;

import java.util.UUID;

/**
 * Klient. Klasa opakowująca osobę do zweryfikowania.
 */
public class Customer {

	private UUID uuid;
	private Person person;

	// visible for Jackson
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
}