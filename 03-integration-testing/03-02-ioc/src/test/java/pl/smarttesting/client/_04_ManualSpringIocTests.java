package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import static pl.smarttesting.client.CustomerVerificationResult.Status.VERIFICATION_FAILED;

/**
 * Kod ze slajdu [IOC / DI i testowanie].
 *
 * Pokazujemy wpierw podejście, gdzie nie musimy w ogóle podnieść kontekstu
 * aplikacyjnego, natomiast jesteśmy w stanie w testach wykorzystać
 * produkcyjny schemat konstruowania obiektów.
 *
 * Z racji używania w schemacie konstruowania obiektów metod fabrykujących, jesteśmy
 * w stanie utworzyć interesujący nas obiekt do testów (CustomerVerifier), podmieniając
 * jedynie część z obiektów, z których on korzysta.
 */
class _04_ManualSpringIocTests {

	@Test
	void manualConfigCall() {
		// Utworzenie obiektu dla testowej konfiguracji
		CustomerVerifier customerVerifier = new TestConfig().testCustomerVerifier();

		// Wywołanie logiki biznesowej
		CustomerVerificationResult result = customerVerifier.verify(tooYoungStefan());

		BDDAssertions.then(result.getStatus()).isEqualTo(VERIFICATION_FAILED);
	}

	Person tooYoungStefan() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "");
	}

}

/*
 * Konfiguracja testowa rozszerzająca schemat produkcyjny. Chcemy osiągnąć sytuację,
 * w której możemy wołać produkcyjne metody schematu, by utworzyć interesujący nas obiekt
 * na potrzeby testów, bez potrzeby uruchamiania kontekstu.
 */
class TestConfig extends _02_Config {

	// Na potrzeby testów. Brak adnotacji = brak obiektu w kontenerze
	IdentificationNumberVerification testIdVerification() {
		// Produkcyjna metoda konfiguracyjna z bazą danych w pamięci
		// Innymi słowy, podmieniamy na potrzeby testów jeden
		// z obiektów (łączący się z prawdziwą bazą danych)
		// w drugi (który posiada bazę danych w pamięci).
		return idVerification(new InMemoryDatabaseAccessor());
	}

	// Na potrzeby testów. Produkcyjna metoda konfiguracyjna z kolekcją weryfikacji
	// Wykorzystujemy produkcyjną metodę, do której przekazujemy kolekcję z pojedynczym
	// elementem weryfikacji, który został przez nas przygotowany dla potrzeb testów.
	CustomerVerifier testCustomerVerifier() {
		return customerVerifier(Collections.singleton(testIdVerification()));
	}
}
