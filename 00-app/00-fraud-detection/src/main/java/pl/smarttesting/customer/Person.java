package pl.smarttesting.customer;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

/**
 * Reprezentuje osobę do zweryfikowania.
 */
public class Person {

	private String name;

	private String surname;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate dateOfBirth;

	private GENDER gender;

	private String nationalIdentificationNumber;

	private STATUS status = STATUS.NOT_STUDENT;

	public Person() {
	}

	public Person(String name, String surname, String dateOfBirth, GENDER gender, String nationalIdentificationNumber) {
		this(name, surname, LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd-MM-yyyy")), gender, nationalIdentificationNumber);
	}

	public Person(String name, String surname, LocalDate dateOfBirth, GENDER gender, String nationalIdentificationNumber) {
		this.name = name;
		this.surname = surname;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.nationalIdentificationNumber = nationalIdentificationNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setGender(GENDER gender) {
		this.gender = gender;
	}

	public void setNationalIdentificationNumber(String nationalIdentificationNumber) {
		this.nationalIdentificationNumber = nationalIdentificationNumber;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
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
		MALE, FEMALE, OTHER
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