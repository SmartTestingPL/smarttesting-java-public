package pl.smarttesting.order;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

/**
 * Bazowa klasa testowa, z której dziedziczą klasy testowe w pakiecie. Rozwiązanie polecane
 * szczególnie kiedy mamy wiele klas testowych wymagających takiego samego setupu.
 */
public abstract class LoanOrderTestBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanOrderTest.class);

	protected Customer aStudent() {
		Customer customer = new Customer(UUID
				.randomUUID(), new Person("Jan", "Nowicki",
				LocalDate.of(1996, 8, 28), Person.GENDER.MALE,
				"96082812079"));
		customer.student();
		return customer;
	}

	// Przykład metody pomocniczej tworzącej obiekty wykorzystywane w różnych testach
	protected Customer aCustomer() {
		return new Customer(UUID
				.randomUUID(), new Person("Maria", "Kowalska",
				LocalDate.of(1989, 3, 10), Person.GENDER.FEMALE,
				"89031013409"));
	}


	// Metoda tear-down wywoływana po każdym teście.
	// W JUnit5 możemy przekazywać obiekt `TestInfo` do wszystkich metod setup i tear-down.
	// Oczywiście, można mieć tu jakiś sensowny biznesowy scenariusz zamiast logowania;
	// to co chcemy pokazać, to że narzędzia umożliwiają nam wywołanie danej metody po każdym teście.
	@AfterEach
	protected void afterEachTest(TestInfo testInfo) {
		LOGGER.info("Finished test: {}.", testInfo.getDisplayName());
	}
}
