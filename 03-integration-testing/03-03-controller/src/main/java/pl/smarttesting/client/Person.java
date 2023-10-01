package pl.smarttesting.client;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	private boolean isStudent;

	@JsonCreator
	Person(@JsonProperty("uuid") UUID uuid, @JsonProperty("name") String name, @JsonProperty("surname") String surname, @JsonProperty("dateOfBirth") LocalDate dateOfBirth, @JsonProperty("gender") GENDER gender, @JsonProperty("nationalIdentificationNumber") String nationalIdentificationNumber) {
		this.uuid = uuid;
		this.name = name;
		this.surname = surname;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.nationalIdentificationNumber = nationalIdentificationNumber;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public GENDER getGender() {
		return gender;
	}

	public String getNationalIdentificationNumber() {
		return nationalIdentificationNumber;
	}

	boolean isStudent() {
		return isStudent;
	}

	void setStudent(boolean student) {
		isStudent = student;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getAge() {
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

	@Override
	public String toString() {
		return "Person{" +
				"age=" + getAge() +
				'}';
	}
}