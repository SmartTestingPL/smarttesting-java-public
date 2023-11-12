package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.verifier.EventEmitter;

/**
 * Pierwotny test duplikuje logikę weryfikacji w sekcji given. To oznacza, że de facto nic nie testujemy.
 * Wykorzystujemy tę samą logikę do przygotowania obiektu, który oczekujemy na wyjściu. Jeśli zmieni się logika
 * biznesowa oba nasze testy dalej będą przechodzić. Szczerze mówiąc to nawet nie weryfikujemy czy wynik boolowski
 * jest true czy false - po prostu sprawdzamy czy jest taki sam jaki na wejściu.
 */
class Done_NameVerificationTests {

	/**
	 * Możemy sobie wyciągnąć obiekt do przetestowania do pola - testy będą czytelniejsze.
	 */
	NameVerification verification = new NameVerification(new EventEmitter());
	

	@Test
	void should_verify_positively_when_name_is_alphanumeric() {
		then(verification.passes(withValidName())).isTrue();
	}
	
	@Test
	void should_verify_negatively_when_name_is_not_alphanumeric() {
		then(verification.passes(withInvalidName())).isFalse();
	}

	/**
	 * Często zdarza się tak, że jak obiekt w konstruktorze ma kilka parametrów tych samych typów,
	 * to można się pomylić i wstawić nie ten tam gdzie trzeba (np. name i surname). Warto jawnie 
	 * wprowadzić nieprawidłową wartość w każde inne "podejrzane" pole, tak żeby mieć pewność, że testujemy
	 * to, co powinniśmy.
	 */
	private Person withValidName() {
		return new Person("jan", null, LocalDate.now(), GENDER.MALE, "abcdefghijk");
	}
	
	private Person withInvalidName() {
		return new Person(null, "Kowalski", LocalDate.now(), GENDER.MALE, "abcdefghijk");
	}

}
