package pl.smarttesting.client;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

/**
 * Reprezentuje osobę do zweryfikowania.
 */
public class Person {

	private final UUID uuid;
	private final String name;
	private final String surname;
	private final LocalDate dateOfBirth;
	private final GENDER gender;
	private final String nationalIdentificationNumber;

	Person(UUID uuid, String name, String surname, LocalDate dateOfBirth, GENDER gender, String nationalIdentificationNumber) {
		this.uuid = uuid;
		this.name = name;
		this.surname = surname;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.nationalIdentificationNumber = nationalIdentificationNumber;
	}

	UUID getUuid() {
		return uuid;
	}

	String getName() {
		return name;
	}

	String getSurname() {
		return surname;
	}

	LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	GENDER getGender() {
		return gender;
	}

	String getNationalIdentificationNumber() {
		return nationalIdentificationNumber;
	}

	int getAge() {
		LocalDate currentDate = LocalDate.now();
		if (dateOfBirth != null) {
			return Period.between(dateOfBirth, currentDate).getYears();
		}
		else {
			throw new IllegalStateException("Date of birth cannot be null");
		}
	}

	public enum GENDER {
		MALE, FEMALE;
	}
}