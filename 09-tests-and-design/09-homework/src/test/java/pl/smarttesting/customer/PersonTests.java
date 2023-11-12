package pl.smarttesting.customer;

import static org.assertj.core.api.BDDAssertions.then;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person.GENDER;

@Homework("Zrefaktoruj ten test. Czy na pewno musimy tyle weryfikować?")
class PersonTests { 
	
	@Test
	void should_work_with_getters() {
		Person person = new Person("name", "surname", LocalDate.of(2001, 11, 1), GENDER.MALE, "1234567890");
		
		then(person.getName()).isEqualTo("name");
		then(person.getSurname()).isEqualTo("surname");
		then(person.getGender()).isEqualTo(GENDER.MALE);
		then(person.getNationalIdentificationNumber()).isEqualTo("1234567890");
		then(person.getAge()).isGreaterThanOrEqualTo(9);
	}

}
