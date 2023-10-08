package pl.smarttesting.e2e.customer;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.PersonProperties;

import static pl.smarttesting.e2e.customer.Person.STATUS.STUDENT;

/**
 * Przykład buildera do setupu testów.
 */
public class CustomerBuilder {
	Fairy fairy = Fairy.create(Locale.forLanguageTag("PL"));

	private UUID uuid = UUID.randomUUID();
	private String name = "Anna";
	private String surname = "Kowalska";
	private LocalDate dateOfBirth = LocalDate.of(1978, 9, 12);
	private Person.GENDER gender = Person.GENDER.FEMALE;
	private String nationalIdentificationNumber = "78091211463";
	private Person.STATUS status = Person.STATUS.NOT_STUDENT;

	public static CustomerBuilder create() {
		return new CustomerBuilder();
	}

	// Klient generowany przy użyciu biblioteki JFairy
	public CustomerBuilder adultMale() {
		com.devskiller.jfairy.producer.person.Person person = fairy
				.person(PersonProperties.male(), PersonProperties.minAge(21));
		return withName(person.getFirstName())
				.withSurname(person.getLastName())
				.withDateOfBirth(person.getDateOfBirth())
				.withGender(Person.GENDER.valueOf(person.getSex().name()))
				.withNationalIdentificationNumber(person
						.getNationalIdentificationNumber());
	}

	public CustomerBuilder withUUID(UUID uuid) {
		this.uuid = uuid;
		return this;
	}

	public CustomerBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public CustomerBuilder withSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public CustomerBuilder withDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
		return this;
	}

	public CustomerBuilder withDateOfBirth(int year, int month, int day) {
		dateOfBirth = LocalDate.of(year, month, day);
		return this;
	}

	public CustomerBuilder withGender(Person.GENDER gender) {
		this.gender = gender;
		return this;
	}

	public CustomerBuilder withNationalIdentificationNumber(String nationalIdentificationNumber) {
		this.nationalIdentificationNumber = nationalIdentificationNumber;
		return this;
	}

	public CustomerBuilder withStatus(Person.STATUS status) {
		this.status = status;
		return this;
	}

	public Customer build() {
		Customer customer = new Customer(uuid, new Person(name, surname, dateOfBirth, gender,
				nationalIdentificationNumber));
		if (STUDENT.equals(status)) {
			customer.student();
		}
		return customer;
	}
}
