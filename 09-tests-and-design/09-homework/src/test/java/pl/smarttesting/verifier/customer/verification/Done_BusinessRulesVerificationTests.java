package pl.smarttesting.verifier.customer.verification;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;

import pl.smarttesting.customer.Person;
import pl.smarttesting.customer.Person.GENDER;
import pl.smarttesting.verifier.EventEmitter;
import pl.smarttesting.verifier.VerificationEvent;

/**
 * Pierwotny test był bardzo źle napisany. Nie dość, że nie wiemy, co testujemy patrząc na nazwę 
 * metody testowej to nawet nie wiemy gdzie jest sekcja when. Kod jest bardzo nieczytelny i robi
 * stanowczo za dużo. Używa też niepotrzebnych konkretnych weryfikacji
 */
class Done_BusinessRulesVerificationTests {

	EventEmitter emitter = mock(EventEmitter.class);

	VerifierManagerImpl manager = mock(VerifierManagerImpl.class);

	/**
	 * Przykład wykorzystania mapy do testów parametryzowanych. W zależności od tego, który 
	 * przypadek błędnych weryfikacji chcemy przetestować (Adres, Imię, Nazwisko itd.) wybieramy
	 * odpowiednią metodę ustawiającej stan mocka.
	 */
	private static Map<String, Consumer<VerifierManagerImpl>> faultyVerificationSetup = Map.of(
			"Address", manager -> given(manager.verifyAddress(any())).willReturn(false),
			"Name", manager -> given(manager.verifyName(any())).willReturn(false),
			"Surname", manager -> given(manager.verifySurname(any())).willReturn(false),
			"Phone", manager -> given(manager.verifyPhone(any())).willReturn(false),
			"Tax", manager -> given(manager.verifyTaxInformation(any())).willReturn(false)
			);

	/**
	 * Przed każdym testem ustawiamy domyślne wartości mocka na true. Moglibyśmy wykorzystać 
	 * dedykowaną w tym celu klasę Answer i przekazać ją przy tworzeniu mocka, natomiast chcemy
	 * uprościć ten przypadek, żeby nie wchodzić w detale implementacyjne Mockito.
	 */
	@BeforeEach
	void setup() {
		given(manager.verifyAddress(any())).willReturn(true);
		given(manager.verifyName(any())).willReturn(true);
		given(manager.verifySurname(any())).willReturn(true);
		given(manager.verifyPhone(any())).willReturn(true);
		given(manager.verifyTaxInformation(any())).willReturn(true);
	}

	@Test
	void should_verify_positively_a_person_when_default_verifier_manager_rules_are_applied() {
		BusinessRulesVerification verification = new BusinessRulesVerification(emitter, new VerifierManagerImpl());

		then(verification.passes(person())).isTrue();
		verify(emitter).emit(new VerificationEvent(true));
	}
	
	/**
	 * Test parametryzowany przypadków negatywnych. Na podstawie typu weryfikacji ustawi mocka w odpowiednim stanie.
	 * 
	 * @param verificationType typ weryfikacji, który oczekujemy, że się nie zwaliduje
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Address", "Name", "Surname", "Phone", "Tax" })
	void should_verify_negatively_when_a_person_has_an_illegal_verification_of_type(String verificationType) {
		faultyVerificationSetup.get(verificationType).accept(manager);
		
		BusinessRulesVerification verification = new BusinessRulesVerification(emitter, manager);

		then(verification.passes(person())).isFalse();
		verify(emitter).emit(new VerificationEvent(false));
	}


	private Person person() {
		return new Person("J", "K", LocalDate.now(), GENDER.MALE, "1234567890");
	}

}