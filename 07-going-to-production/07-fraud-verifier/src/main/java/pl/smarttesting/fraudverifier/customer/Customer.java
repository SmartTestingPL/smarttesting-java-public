package pl.smarttesting.fraudverifier.customer;

import java.util.UUID;

/**
 * Klient. Klasa opakowująca osobę do zweryfikowania.
 */
public class Customer {

	private final UUID uuid;
	private final Person person;

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