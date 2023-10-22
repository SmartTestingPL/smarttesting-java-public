package pl.smarttesting.customer;

import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

/**
 * Reprezentuje osobę do zweryfikowania.
 */
public class Person {

	private final String name;
	private final String surname;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "dd-MM-yyyy")
	private final LocalDate dateOfBirth;
	private final GENDER gender;
	private final String nationalIdentificationNumber;
	private STATUS status = STATUS.NOT_STUDENT;

	@JsonCreator
	public Person(@JsonProperty("name") String name, @JsonProperty("surname") String surname, @JsonProperty("dateOfBirth") LocalDate dateOfBirth, @JsonProperty("gender") GENDER gender, @JsonProperty("nationalIdentificationNumber") String nationalIdentificationNumber) {
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
		return STATUS.STUDENT.equals(status);
	}

	void student() {
		status = STATUS.STUDENT;
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
		MALE, FEMALE
	}

	public enum STATUS {
		STUDENT, NOT_STUDENT;
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", surname='" + surname + '\'' +
				", dateOfBirth=" + dateOfBirth +
				", gender=" + gender +
				", nationalIdentificationNumber='" + nationalIdentificationNumber + '\'' +
				", status=" + status +
				'}';
	}
}