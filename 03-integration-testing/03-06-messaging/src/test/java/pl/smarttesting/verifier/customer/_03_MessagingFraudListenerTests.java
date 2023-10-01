package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.SmartTestingApplication;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.awaitility.Awaitility.await;

/**
 * W tej klasie testowej piszemy test dla komponentu nasłuchującego wiadomości z brokera.
 * Przed uruchomieniem właściwych testów, dzięki użyciu narzędzia
 * Testcontainers odpalimy w kontenerze Dockerowym, brokera RabbitMQ.
 */
@SpringBootTest(classes = _03_MessagingFraudListenerTests.TestConfig.class)
@ActiveProfiles("container")
@Testcontainers
class _03_MessagingFraudListenerTests {

	/**
	 * Uruchomienie kontenera z brokerem na losowym porcie przed uruchomieniem testów.
	 */
	@Container
	private static final RabbitMQContainer CONTAINER = new RabbitMQContainer("rabbitmq:3.8.27-management-alpine")
			.withReuse(true);

	/**
	 * W bloku statycznym ustawiamy zmienną RABBIT_PORT wartością portu, na którym uruchomiony jest
	 * broker. Tak wykorzystany losowy port będzie zaczytany w konfiguracji
	 * application-container.yaml.
	 */
	static {
		CONTAINER.start();
		System.setProperty("RABBIT_PORT", String.valueOf(CONTAINER.getFirstMappedPort()));
	}

	/**
	 * Test weryfikujący czy nasłuchiwacz wiadomości, potrafi pobrać wiadomość
	 * z brokera i zapisać do bazy danych. Na potrzeby szkolenia i prostoty
	 * przykładu nie stawiamy bazy danych w kontenerze, żeby zweryfikować czy
	 * w bazie faktycznie odłożył się klient. Można jednak byłoby rozważyć taki test,
	 * gdyż klasa nasłuchująca jest bardzo prosta i rozbicie jej na dwa osobne testy
	 * wydaje się nadgorliwością.
	 *
	 * @param rabbitTemplate - klient do komunikacji z RabbitMQ
	 * @param repository - wstrzyknięte repozytorium z dostępem do bazy danych
	 */
	@Test
	void should_store_a_fraud_when_a_customer_verification_was_received_from_an_external_system(@Autowired RabbitTemplate rabbitTemplate, @Autowired VerificationRepository repository) {
		Customer customer = stefania();

		// Wysyłamy wiadomość na kolejkę
		rabbitTemplate.convertAndSend("fraudInput", "#", failedStefaniaVerification(customer));

		// Komunikacja jest asynchroniczna, oczekujemy, że w końcu wiadomość zostanie
		// skonsumowana i klient zostanie zapisany w bazie danych. Do tego celu
		// wykorzystujemy bibliotekę Awaitility (metoda `await().until(...)`)
		await().until(() -> repository.findByUserId(customer.getUuid()).isPresent());
	}

	private CustomerVerification failedStefaniaVerification(Customer customer) {
		return new CustomerVerification(customer.getPerson(), CustomerVerificationResult.failed(customer.getUuid()));
	}

	private Customer stefania() {
		return new Customer(UUID.fromString("789b58b8-957b-4f76-8046-1287382b2e64"), youngStefania());
	}

	private Person youngStefania() {
		return new Person("Stefania", "Stefanowska", LocalDate.now(), Person.GENDER.FEMALE, "18210145358");
	}

	@Configuration
	@Import(SmartTestingApplication.class)
	static class TestConfig {

	}
}
