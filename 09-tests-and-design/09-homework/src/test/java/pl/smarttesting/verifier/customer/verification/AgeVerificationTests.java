package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.VerificationEvent;

@Homework("Czy ten test na pewno weryfikuje... cokolwiek?")
class AgeVerificationTests {

	@Test
	void should_emit_event_when_date_of_birth_invalid() {
		EventEmitter emitter = mock(EventEmitter.class);
		AgeVerification verification = new AgeVerification(emitter);
		
		thenThrownBy(() -> {
			verification.passes(new Person("jan", "kowalski", null, GENDER.MALE, "abcdefghijkl"));
			then(emitter).should().emit(new VerificationEvent(false));
		}).isInstanceOf(IllegalStateException.class);
	}
}