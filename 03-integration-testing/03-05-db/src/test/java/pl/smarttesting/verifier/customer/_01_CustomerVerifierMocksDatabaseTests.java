package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static pl.smarttesting.verifier.customer.CustomerVerificationResult.Status.*;

/**
 * Klasa testowa pokazująca jak testując serwis aplikacyjny `CustomerVerifier`,
 * możemy zamockować komunikację z bazą danych.
 */
public class _01_CustomerVerifierMocksDatabaseTests {

	VerificationRepository repository = BDDMockito.mock(VerificationRepository.class);

	/**
	 * W przypadku ówczesnego zapisu klienta w bazie danych, chcemy się upewnić, że
	 * nie dojdzie do ponownego zapisu klienta w bazie danych.
	 */
	@Test
	void should_return_stored_customer_result_when_customer_already_verified() {
		VerifiedPerson verifiedPerson = givenAnExistingVerifiedPerson();

		CustomerVerificationResult result = customerVerifierWithExceptionThrowingBik()
				.verify(new Customer(verifiedPerson.getUserId(), nonFraudPerson()));

		then(result.getUserId()).as("Must represent the same person")
					.isEqualTo(verifiedPerson.getUserId());
		then(result.getStatus()).isEqualTo(VERIFICATION_PASSED);
		// chcemy się upewnić, że NIE doszło do zapisu w bazie danych
		BDDMockito.then(repository).should(never()).save(any(VerifiedPerson.class));
	}

	/**
	 * W przypadku braku zapisu klienta w bazie danych, chcemy się upewnić, że
	 * dojdzie do zapisu w bazie danych.
	 */
	@Test
	void should_calculate_customer_result_when_customer_not_previously_verified() {
		UUID newPersonId = UUID.randomUUID();

		CustomerVerificationResult result = customerVerifierWithPassingBik()
				.verify(new Customer(newPersonId, nonFraudPerson()));

		then(result.getUserId()).as("Must represent the same person").isEqualTo(newPersonId);
		then(result.getStatus()).isEqualTo(VERIFICATION_PASSED);
		// chcemy się upewnić, że doszło do zapisu w bazie danych
		BDDMockito.then(repository).should().save(argThat(arg -> arg.getUserId().equals(newPersonId)));
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

	private VerifiedPerson givenAnExistingVerifiedPerson() {
		VerifiedPerson verifiedPerson = verifiedNonFraud();
		// Symulujemy, że osoba została zapisana w bazie danych wcześniej
		BDDMockito.given(repository.findByUserId(verifiedPerson.getUserId()))
				.willReturn(Optional.of(verifiedPerson));
		return verifiedPerson;
	}

	private Person nonFraudPerson() {
		return new Person("Ucziwy", "Ucziwowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}

	private VerifiedPerson verifiedNonFraud() {
		return new VerifiedPerson(UUID.randomUUID(), "1234567890", VERIFICATION_PASSED);
	}
}
