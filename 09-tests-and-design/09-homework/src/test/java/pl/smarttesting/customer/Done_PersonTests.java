package pl.smarttesting.customer;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;

/**
 * Pierwotny test testował gettery i settery, które jedynie zwracały ustawioną wartość.
 * To, co na pewno powinniśmy przetestować to sposób liczenia wieku - tam nie jest zwracana 
 * ustawiona wartość wieku tylko jest on wyliczony.
 */
class Done_PersonTests { 
	
	/**
	 * Przykład udanego wyliczenia wieku.
	 */
	@Test
	void should_calculate_age_of_person() {
		Person person = new Person("name", "surname", LocalDate.of(2001, 11, 1), GENDER.MALE, "1234567890") {

			@Override
			LocalDate currentDate() {
				return LocalDate.of(2011, 11, 1);
			}
			
		};
		
		then(person.getAge()).isEqualTo(10);
	}
	
	/**
	 * Przykład wyliczenia wieku, który zakończy się rzuceniem wyjątku.
	 */
	@Test
	void should_fail_to_calculate_the_age_of_person_when_date_is_null() {
		Person person = new Person("name", "surname", null, GENDER.MALE, "1234567890");
		
		thenThrownBy(() -> person.getAge()).isInstanceOf(IllegalStateException.class);
	}

}
