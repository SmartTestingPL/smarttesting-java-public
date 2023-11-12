package pl.smarttesting.verifier.customer.verification;

import static org.mockito.Mockito.mock;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import pl.smarttesting.Homework;
import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.EventEmitter;

@Homework("Czy ten test na pewno jest czytelny? Co on w ogóle testuje? Czyżby wszystkie przypadki błędnych weryfikacji?")
class BusinessRulesVerificationTests {

	@Test
	void testImpl() {
		EventEmitter e = mock(EventEmitter.class);
		VerifierManagerImpl i = Mockito.mock(VerifierManagerImpl.class);
		// Jan should fail
		Mockito.when(i.verifyName(Mockito.argThat(new ArgumentMatcher<Person>() {

			@Override
			public boolean matches(Person argument) {
				return argument.getName().equals("Jan");
			}
		}))).thenReturn(false);
		BusinessRulesVerification v = new BusinessRulesVerification(e, i);
		BDDMockito.given(i.verifyAddress(BDDMockito.any(Person.class))).willReturn(true);
		Mockito.when(i.verifyPhone(BDDMockito.any(Person.class))).thenReturn(true);
		BDDMockito.given(i.verifyTaxInformation(BDDMockito.any(Person.class))).willReturn(true);
		Person p = new Person("Jan", "Kowalski", LocalDate.now(), null, "12309279124123");
		Mockito.when(i.verifySurname(BDDMockito.any(Person.class))).thenReturn(true);
		boolean passes = v.passes(p);
		Assertions.assertFalse(passes);
		Mockito.verify(i).verifyName(Mockito.argThat(new ArgumentMatcher<Person>() {

			@Override
			public boolean matches(Person argument) {
				return argument.getName().equals("Jan");
			}
		}));
		Mockito.reset(i);
		Mockito.when(i.verifyName(BDDMockito.any(Person.class))).thenReturn(true);
		BDDMockito.given(i.verifyAddress(BDDMockito.any(Person.class))).willReturn(false);
		passes = v.passes(p);
		Assertions.assertFalse(passes);
		Mockito.reset(i);
		BDDMockito.given(i.verifyAddress(BDDMockito.any(Person.class))).willReturn(true);
		Mockito.when(i.verifyPhone(BDDMockito.any(Person.class))).thenReturn(false);		
		passes = v.passes(p);
		Assertions.assertFalse(passes);
		Mockito.reset(i);
		Mockito.when(i.verifyPhone(BDDMockito.any(Person.class))).thenReturn(true);		
		BDDMockito.given(i.verifyTaxInformation(BDDMockito.any(Person.class))).willReturn(false);
		passes = v.passes(p);
		Assertions.assertFalse(passes);
	}
}