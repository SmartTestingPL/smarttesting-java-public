package pl.smarttesting.loanorders.customer;

import java.time.LocalDate;
import java.time.Period;

/**
 * Reprezentuje osobę do zweryfikowania.
 */
public class Person {

	private String name;
	private String surname;
	private LocalDate dateOfBirth;
	private GENDER gender;
	private String nationalIdentificationNumber;
	private STATUS status = STATUS.NOT_STUDENT;

	// available for Jackson
	public Person() {

	}

	public Person(String name, String surname, LocalDate dateOfBirth, GENDER gender, String nationalIdentificationNumber) {
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
		STUDENT, NOT_STUDENT
	}
}