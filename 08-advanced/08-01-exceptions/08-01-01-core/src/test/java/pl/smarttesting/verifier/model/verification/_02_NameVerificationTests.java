package pl.smarttesting.verifier.model.verification;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

class _02_NameVerificationTests {

	/**
	 * Test, w którym weryfikujemy czy został rzucony bardzo generyczny wyjątek
	 * {@link NullPointerException}.
	 *
	 * Test ten przechodzi nam przypadkowo, gdyż NPE leci w innym miejscu w kodzie
	 * niż się spodziewaliśmy.
	 *
	 * Uruchamiając ten test nie widzimy żeby zalogowała nam się linijka z kasy
	 * {@link _01_NameVerification}...
	 */
	@Test
	void should_throw_an_exception_when_checking_verification() {
		thenThrownBy(() ->
				new _01_NameVerification().passes(anna()))
				.isInstanceOf(NullPointerException.class);
	}

	/**
	 * Poprawiona wersja poprzedniego testu, gdzie tym razem zweryfikujemy
	 * zawartość wiadomości w rzuconym wyjątku.
	 *
	 * Odkomentuj {@link Disabled}, żeby zobaczyć, że test się wysypuje, gdyż
	 * nie jest wołana nasza wersja {@link NullPointerException}, tylko domyślna,
	 * w momencie wołania metody {@code toString()} na wartości {@code null}
	 * zwracanej przez {@link Person#getGender()}.
	 *
	 * Problem polega na tym, że w konstruktorze {@link Person} ktoś zapomniał ustawić
	 * pola {@code gender}.
	 */
	@Disabled
	@Test
	void should_throw_an_exception_when_checking_verification_only() {
		thenThrownBy(() ->
				new _01_NameVerification().passes(anna()))
				.isInstanceOf(NullPointerException.class)
				.hasMessage("Name cannot be null");
	}

	/**
	 * W momencie, w którym nasza aplikacja rzuca wyjątki domenowe, wtedy nasz test
	 * może po prostu spróbować go wyłapać.
	 *
	 * Odkomentuj {@link Disabled}, żeby zobaczyć, że test się wysypuje, gdyż wyjątek,
	 * który poleci to {@link NullPointerException}, a nie {@link _04_VerificationException}.
	 */
	@Disabled
	@Test
	void should_fail_verification_when_name_is_invalid() {
		thenThrownBy(() ->
				new _05_NameWithCustomExceptionVerification().passes(anna()))
				.isInstanceOf(_04_VerificationException.class);
	}

	/**
	 * Koncepcyjnie to samo co powyżej. Do zastosowania w momencie, w którym
	 * nie posiadacie dedykowanych bibliotek do asercji, takich jak np. AssertJ.
	 *
	 * Łapiemy w {@code try {...} catch {...} } wywołanie metody, która powinna rzucić wyjątek.
	 * Koniecznie należy wywalić test, jeśli wyjątek nie zostanie rzucony!!!
	 *
	 * W sekcji {@code catch} możemy wykonać dodatkowe asercje na rzuconym wyjątku.
	 */
	@Disabled
	@Test
	void should_fail_verification_when_name_is_invalid_and_assertion_is_done_manually() {
		try {
			new _05_NameWithCustomExceptionVerification().passes(anna());
			// Koniecznie należy wywalić test, jeśli wyjątek nie zostanie rzucony!!!
			fail("Should fail the verification");
		} catch (_04_VerificationException ex) {
			// Dodatkowe asercje błędu jeśli potrzebne
		}
	}

	private Person anna() {
		return new Person("Anna", "Smith", LocalDate.now(),
				Person.GENDER.FEMALE, "00000000000");
	}
}