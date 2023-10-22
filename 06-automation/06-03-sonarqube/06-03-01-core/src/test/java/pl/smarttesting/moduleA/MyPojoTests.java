package pl.smarttesting.moduleA;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Test pokazujący testowanie getterów i setterów. Jeśli w tych metodach nie ma specjalnej logiki
 * najlepiej nie pisać takich testów.
 */
class MyPojoTests {

	@Test
	void should_test_getters_and_setters() {
		MyPojo myPojo = new MyPojo();
		myPojo.setAge(10);
		myPojo.setName("Name");
		myPojo.setSurname("Surname");

		then(myPojo.getAge()).isEqualTo(10);
		then(myPojo.getName()).isEqualTo("Name");
		then(myPojo.getSurname()).isEqualTo("Surname");
	}
}