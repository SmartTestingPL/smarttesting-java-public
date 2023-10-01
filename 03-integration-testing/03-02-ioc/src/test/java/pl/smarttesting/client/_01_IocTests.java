package pl.smarttesting.client;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

class _01_IocTests {

	/**
	 * Kod przedstawiony na slajdzie [W jaki sposób tworzysz obiekty?].
	 * Przedstawiamy tu ręczne utworzenie drzewa zależności obiektów.
	 * Trzeba pamiętać o odpowiedniej kolejności utworzenia obiektów oraz
	 * w jednym miejscu mieszamy tworzenie i realizacje akcji biznesowej
	 * wywołanie:
	 *
	 * new CustomerVerifier(...).verify(...);
	 */
	@Test
	void manualObjectGeneration() {
		// Tworzenie AgeVerification
		HttpCallMaker httpCallMaker = new HttpCallMaker();
		DatabaseAccessor accessor = new DatabaseAccessor();
		AgeVerification ageVerification = new AgeVerification(httpCallMaker, accessor);

		// Tworzenie IdentificationNumberVerification
		IdentificationNumberVerification idVerification = new IdentificationNumberVerification(accessor);

		// Tworzenie NameVerification
		EventEmitter eventEmitter = new EventEmitter();
		NameVerification nameVerification = new NameVerification(eventEmitter);

		// Wywołanie logiki biznesowej
		CustomerVerificationResult result = new CustomerVerifier(ageVerification, idVerification, nameVerification).verify(stefan());

		// Asercja
		BDDAssertions.then(result.getStatus()).isEqualTo(CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	Person stefan() {
		return new Person(UUID.randomUUID(), "", "", LocalDate.now(), Person.GENDER.MALE, "");
	}
}


