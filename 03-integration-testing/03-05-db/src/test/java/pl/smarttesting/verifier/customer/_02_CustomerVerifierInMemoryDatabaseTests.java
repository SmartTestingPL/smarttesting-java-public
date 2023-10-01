package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Klasa testowa pokazująca jak testując serwis aplikacyjny `CustomerVerifier`, możemy użyć bazy
 * danych w pamięci.
 */
class _02_CustomerVerifierInMemoryDatabaseTests {

	_02_InMemoryVerificationRepository repository = new _02_InMemoryVerificationRepository();

	@Test
	void should_return_cached_customer_result_when_customer_already_verified() {
		VerifiedPerson verifiedPerson = givenAnExistingVerifiedPerson();
		// Przed uruchomieniem metody do przetestowania,
		// upewniamy się, że w bazie danych istnieje wpis dla danego użytkownika
		then(repository.findByUserId(verifiedPerson.getUserId()))
				.as("Successfully stored person in the database").isPresent();

		CustomerVerificationResult result = customerVerifierWithExceptionThrowingBik()
				.verify(new Customer(verifiedPerson.getUserId(), nonFraudPerson()));

		then(result.getUserId()).as("Must represent the same person").isEqualTo(verifiedPerson.getUserId());
		then(result.getStatus()).isEqualTo(CustomerVerificationResult.Status.VERIFICATION_PASSED);
	}

	@Test
	void should_calculate_customer_result_when_customer_not_previously_verified() {
		UUID newPersonId = UUID.randomUUID();
		// Przed uruchomieniem metody do przetestowania,
		// upewniamy się, że w bazie danych NIE istnieje wpis dla danego użytkownika
		then(repository.findByUserId(newPersonId))
				.as("There's no person present").isNotPresent();

		CustomerVerificationResult result = customerVerifierWithPassingBik()
				.verify(new Customer(newPersonId, nonFraudPerson()));

		then(result.getUserId()).as("Must represent the same person").isEqualTo(newPersonId);
		then(result.getStatus()).isEqualTo(CustomerVerificationResult.Status.VERIFICATION_PASSED);
		// Po uruchomieniu metody do przetestowania,
		// upewniamy się, że w bazie danych istnieje wpis dla danego użytkownika
		then(repository.findByUserId(newPersonId)).as("Person got cached").isPresent();
	}

	/**
	 * Testowa implementacja serwisu {@code CustomerVerifier}.
	 * Jeśli zapis w bazie miał już miejsce to nie powinniśmy wołać BIKa.
	 * Jeśli BIK zostanie wywołany to chcemy rzucić wyjątek.
	 * @return implementacja {@link CustomerVerifier} z nadpisanym serwisem kontaktującym się z BIKiem
	 */
	private CustomerVerifier customerVerifierWithExceptionThrowingBik() {
		return new CustomerVerifier(new ExceptionThrowingBikVerifier(), Collections.emptySet(), repository);
	}

	/**
	 * Testowa implementacja serwisu {@code CustomerVerifier}, z nadpisaną
	 * implementacją klienta BIK. Klient ten zawsze zwraca, że
	 * @return implementacja {@link CustomerVerifier} z nadpisanym serwisem kontaktującym się z BIKiem
	 */
	private CustomerVerifier customerVerifierWithPassingBik() {
		return new CustomerVerifier(new AlwaysPassingBikVerifier(), Collections.emptySet(), repository);
	}

	/**
	 * Zwracamy zweryfikowaną osobę, która zostaje też zapisana w bazie danych.
	 *
	 * @return zweryfikowana osoba
	 */
	private VerifiedPerson givenAnExistingVerifiedPerson() {
		VerifiedPerson verifiedPerson = verifiedNonFraud();
		repository.save(verifiedPerson);
		return verifiedPerson;
	}

	private Person nonFraudPerson() {
		return new Person("Ucziwy", "Ucziwowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}

	private VerifiedPerson verifiedNonFraud() {
		return new VerifiedPerson(UUID.randomUUID(), "1234567890", CustomerVerificationResult.Status.VERIFICATION_PASSED);
	}
}

