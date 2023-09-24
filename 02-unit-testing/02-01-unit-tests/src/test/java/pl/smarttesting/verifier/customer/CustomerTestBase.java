package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.UUID;

import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import static pl.smarttesting.customer.Person.STATUS.STUDENT;

/**
 * Klasa bazowa z przykładem buildera obiektu wykorzystywanego w teście.
 */
class CustomerTestBase {

	static CustomerBuilder builder() {
		return new CustomerBuilder();
	}


	// Przykład buildera do setupu testów.
	static class CustomerBuilder {

		private UUID uuid = UUID.randomUUID();
		private String name = "Anna";
		private String surname = "Kowalska";
		private LocalDate dateOfBirth = LocalDate.of(1978, 9, 12);
		private Person.GENDER gender = Person.GENDER.FEMALE;
		private String nationalIdentificationNumber = "78091211463";
		private Person.STATUS status = Person.STATUS.NOT_STUDENT;

		CustomerBuilder withUUID(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		CustomerBuilder withName(String name) {
			this.name = name;
			return this;
		}

		CustomerBuilder withSurname(String surname) {
			this.surname = surname;
			return this;
		}

		CustomerBuilder withDateOfBirth(LocalDate dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
			return this;
		}

		CustomerBuilder withDateOfBirth(int year, int month, int day) {
			dateOfBirth = LocalDate.of(year, month, day);
			return this;
		}

		CustomerBuilder withGender(Person.GENDER gender) {
			this.gender = gender;
			return this;
		}

		CustomerBuilder withNationalIdentificationNumber(String nationalIdentificationNumber) {
			this.nationalIdentificationNumber = nationalIdentificationNumber;
			return this;
		}

		CustomerBuilder withStatus(Person.STATUS status) {
			this.status = status;
			return this;
		}

		Customer build() {
			Customer customer = new Customer(uuid, new Person(name, surname, dateOfBirth, gender,
					nationalIdentificationNumber));
			if (STUDENT.equals(status)) {
				customer.student();
			}
			return customer;
		}
	}
}
