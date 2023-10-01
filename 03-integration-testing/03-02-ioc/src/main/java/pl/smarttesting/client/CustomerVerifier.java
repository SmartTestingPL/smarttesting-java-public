package pl.smarttesting.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
public class CustomerVerifier {

	private final Set<Verification> verifications;

	public CustomerVerifier(Verification... verifications) {
		this.verifications = new HashSet<>(Arrays.asList(verifications));
	}

	public CustomerVerifier(Set<Verification> verfications) {
		this.verifications = verfications;
	}

	/**
	 * Główna metoda biznesowa. Weryfikuje czy dana osoba jest oszustem.
	 * @param person - osoba do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	CustomerVerificationResult verify(Person person) {
		if (verifications.stream().allMatch(Verification::passes)) {
			return CustomerVerificationResult.passed(person.getUuid());
		}
		return CustomerVerificationResult.failed(person.getUuid());
	}
}

/**
 * Weryfikacja po wieku.
 *
 * Na potrzeby scenariusza lekcji, brak prawdziwej implementacji.
 * Klasa symuluje połączenie po HTTP i po bazie danych.
 */
class AgeVerification implements Verification {
	private final HttpCallMaker maker;
	private final DatabaseAccessor accessor;

	AgeVerification(HttpCallMaker maker, DatabaseAccessor accessor) {
		this.maker = maker;
		this.accessor = accessor;
	}
}

/**
 * Weryfikacja po numerze pesel.
 *
 * Na potrzeby scenariusza lekcji, brak prawdziwej implementacji.
 * Klasa symuluje połączenie po bazie danych.
 */
class IdentificationNumberVerification implements Verification {
	private final DatabaseAccessor accessor;

	IdentificationNumberVerification(DatabaseAccessor accessor) {
		this.accessor = accessor;
	}
}

/**
 * Weryfikacja po nazwisku.
 *
 * Na potrzeby scenariusza lekcji, brak prawdziwej implementacji.
 * Klasa symuluje połączenie po brokerze wiadomości.
 */
class NameVerification implements Verification {
	private final EventEmitter eventEmitter;

	NameVerification(EventEmitter eventEmitter) {
		this.eventEmitter = eventEmitter;
	}
}

/**
 * Klasa udająca klasę łączącą się po HTTP.
 */
class HttpCallMaker {

}

/**
 * Klasa udająca klasę łączącą się po bazie danych.
 */
class DatabaseAccessor {

}

/**
 * Klasa udająca klasę łączącą się po brokerze wiadomości.
 */
class EventEmitter {

}