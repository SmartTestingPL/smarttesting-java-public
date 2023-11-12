package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.VerificationEvent;

/**
 * Pierwotny test ma dobry zamiary, ale wykonanie niestety takowe nie jest...
 * 
 * Zamieniając w pierwotnym teście warunki weryfikacji mocka widzimy, że test dalej przechodzi.
 * Ponadto, okazuje się, że IllegalStateException może polecieć z getAge() (co ma miejsce, jak 
 * przekazujemy nulla). 
 * 
 * Czyli powinniśmy sprawdzić wiadomość wyjątku i napisać dwa scenariusze testowe - jeden dla nulla
 * i jeden dla negatywnego wieku. 
 */
class Done_AgeVerificationTests {
	
	EventEmitter emitter = mock(EventEmitter.class);
	
	AgeVerification verification = new AgeVerification(emitter);

	@Test
	void should_throw_exception_when_date_of_birth_not_set() {		
		thenThrownBy(() -> verification.passes(new Person("jan", "kowalski", null, GENDER.MALE, "abcdefghijkl")))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Date of birth cannot be null");
	}
	
	/**
	 * Ustawiając datę na przyszłość uzyskujemy wartość ujemną wieku. W ten sposób
	 * jesteśmy w stanie zweryfikować, że emitter się wykonał.
	 */
	@Test
	void should_emit_event_when_date_of_birth_negative() {
		thenThrownBy(() -> verification.passes(new Person("jan", "kowalski", LocalDate.now().plusDays(5), GENDER.MALE, "abcdefghijkl")))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Age cannot be negative");
		then(emitter).should().emit(new VerificationEvent(false));
	}
}