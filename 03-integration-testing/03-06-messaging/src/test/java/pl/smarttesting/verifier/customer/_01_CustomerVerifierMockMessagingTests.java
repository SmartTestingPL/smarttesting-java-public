package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;

/**
 * W tej klasie testowej
 * - piszemy test dla CustomerVerifiera i mockujemy komponent wysyłający wiadomości
 * - piszemy test dla komponentu wysyłającego wiadomość
 * - piszemy test dla nasłuchiwacza wiadomości
 */
class _01_CustomerVerifierMockMessagingTests {

	VerificationRepository repository = mock(VerificationRepository.class);

	/**
	 * W tym teście testujemy serwis aplikacyjny, a mockujemy wysyłacza
	 * wiadomości (FraudAlertNortifier). Nie testujemy żadnej integracji,
	 * działamy na mockach. Testy są szybkie - mogłyby być tak napisane
	 * dla corowej części naszej domeny.
	 */
	@Test
	void should_send_a_message_with_fraud_details_when_found_a_fraud_using_mocks() {
		FraudAlertNotifier messaging = mock(FraudAlertNotifier.class);
		Person fraud = fraud();

		alwaysFailingCustomerVerifier(messaging)
				.verify(new Customer(UUID.randomUUID(), fraud));

		BDDMockito.then(messaging).should()
					.fraudFound(argThat(argument -> nationalIdNumberAreEqual(fraud, argument)));
	}

	/**
	 * Przykład testu, w którym testujemy już sam komponent do wysyłki
	 * wiadomości. Mockujemy klienta do rabbita (RabbitTemplate) i
	 * weryfikujemy czy metoda na kliencie się wykonała.
	 *
	 * Nie sprawdzamy żadnej integracji, test niewiele nam daje.
	 */
	@Test
	void should_send_a_message_using_rabbit_template() {
		RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
		CustomerVerification verification = fraudCustomerVerification(fraud(), UUID.randomUUID());

		new MessagingFraudAlertNotifier(rabbitTemplate).fraudFound(verification);

		BDDMockito.then(rabbitTemplate).should().convertAndSend("fraudOutput", "#", verification);
	}

	private boolean nationalIdNumberAreEqual(Person fraud, CustomerVerification argument) {
		return argument.getPerson().getNationalIdentificationNumber().equals(fraud.getNationalIdentificationNumber());
	}

	/**
	 * W tym teście weryfikujemy czy nasłuchiwacz na wiadomości, w momencie uzyskania
	 * wiadomości potrafi zapisać obiekt w bazie danych. Test ten nie integruje się
	 * z brokerem wiadomości więc nie mamy pewności czy potrafimy zdeserializować
	 * wiadomość. Z punktu widzenia nasłuchiwania zapis do bazy danych jest efektem
	 * ubocznym więc, możemy rozważyć użycie mocka.
	 */
	@Test
	void should_store_fraud_when_received_over_messaging() {
		MessagingFraudListener listener = new MessagingFraudListener(repository);
		UUID uuid = UUID.randomUUID();

		listener.onFraud(fraudCustomerVerification(fraud(), uuid));

		BDDMockito.then(repository).should().save(argThat(argument -> argument.getUserId().equals(uuid)));
	}

	private CustomerVerification fraudCustomerVerification(Person fraud, UUID userId) {
		return new CustomerVerification(fraud, new CustomerVerificationResult(userId, CustomerVerificationResult.Status.VERIFICATION_FAILED));
	}

	private CustomerVerifier alwaysFailingCustomerVerifier(FraudAlertNotifier messaging) {
		return new CustomerVerifier(Collections.singleton(new AlwaysFailingVerification()), repository, messaging);
	}

	private Person fraud() {
		return new Person("Fraud", "Fraudowski", LocalDate.now(), Person.GENDER.MALE, "1234567890");
	}
}

