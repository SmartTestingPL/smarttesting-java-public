package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

/**
 * W tej klasie testowej piszemy test dla serwisu CustomerVerifier, który
 * zamiast produkcyjnej instancji komponentu wysyłającego wiadomości,
 * użyje komponentu zawierającego kolejkę w pamięci.
 */
class _02_CustomerVerifierInMemoryMessagingTests {

	VerificationRepository repository = mock(VerificationRepository.class);

	/**
	 * W tym teście wykorzystujemy implementację wysyłacza wiadomości z
	 * brokerem w formie kolejki w pamięci. W momencie, w którym zostaje wysłana wiadomość
	 * ląduje ona w kolejce. Wykorzystując konkretną implementację (a nie interfejs)
	 * jesteśmy w stanie wyciągnąć tę wiadomość i ocenić jej zawartość.
	 *
	 * Testy są szybkie - mogłyby być tak napisane dla corowej części naszej domeny.
	 */
	@Test
	void should_send_a_message_with_fraud_details_when_found_a_fraud() {
		InMemoryMessaging messaging = new InMemoryMessaging();
		Person fraud = fraud();

		alwaysFailingCustomerVerifier(messaging)
				.verify(new Customer(UUID.randomUUID(), fraud));

		CustomerVerification sentVerification = messaging.poll();
		then(sentVerification.getPerson().getNationalIdentificationNumber())
					.isEqualTo(fraud.getNationalIdentificationNumber());
	}

	private CustomerVerifier alwaysFailingCustomerVerifier(FraudAlertNotifier messaging) {
		return new CustomerVerifier(Collections.singleton(new AlwaysFailingVerification()), repository, messaging);
	}

	private Person fraud() {
		return new Person("Fraud", "Fraudowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}

}

