package pl.smarttesting.verifier.customer;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * W tej klasie testowej piszemy test dla komponentu wysyłającego wiadomość do brokera.
 * W teście `_03_CustomerVerifierWithContainersTests` już go przetestowaliśmy, natomiast
 * możemy przetestować go w izolacji. Wówczas w teście samego serwisu, możemy użyć mocka.
 *
 * W ten sposób uzyskujemy ładny test integracyjny z wydzielonym komponentem od wysyłki
 * wiadomości.
 *
 * Przed uruchomieniem właściwych testów, dzięki użyciu narzędzia
 * Testcontainers odpalimy w kontenerze Dockerowym, brokera RabbitMQ.
 */
@SpringBootTest(classes = _04_MessagingFraudAlertNotifierWithContainersTests.TestConfig.class)
@ActiveProfiles("container")
@Testcontainers
class _04_MessagingFraudAlertNotifierWithContainersTests {

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
	 * Test weryfikujący czy wysyłacz wiadomości potrafi wysłać wiadomość
	 * do brokera.
	 *
	 * @param notifier - wysyłacz wiadomości, który chcemy przetestować
	 * @param rabbitTemplate - klient do komunikacji z RabbitMQ
	 */
	@Test
	void should_send_a_message_to_a_broker_when_fraud_was_found(@Autowired MessagingFraudAlertNotifier notifier, @Autowired RabbitTemplate rabbitTemplate) {
		Customer zbigniew = zbigniew();

		notifier.fraudFound(customerVerification(zbigniew));

		Message message = rabbitTemplate.receive("fraudOutput", 100);
		then(message).isNotNull();
		then(messageBody(message)).contains(zbigniewUuid(zbigniew));
	}

	private CustomerVerification customerVerification(Customer customer) {
		return new CustomerVerification(customer.getPerson(), failedResult(customer));
	}

	private CustomerVerificationResult failedResult(Customer zbigniew) {
		return new CustomerVerificationResult(zbigniew.getUuid(), CustomerVerificationResult.Status.VERIFICATION_FAILED);
	}

	private String zbigniewUuid(Customer zbigniew) {
		return "\"userId\":\"" + zbigniew.getUuid().toString();
	}

	private String messageBody(Message message) {
		return new String(message.getBody());
	}

	private Customer zbigniew() {
		return new Customer(UUID.fromString("89c878e3-38f7-4831-af6c-c3b4a0669022"), youngZbigniew());
	}

	private Person youngZbigniew() {
		return new Person("Zbigniew", "Zbigniewowski", LocalDate.now(), Person.GENDER.MALE, "18210116954");
	}


	@Configuration
	@Import(CustomerConfiguration.RabbitMqConfiguration.class)
	@ImportAutoConfiguration(RabbitAutoConfiguration.class)
	static class TestConfig {

		@Bean
		FraudAlertNotifier fraudAlertNotifier(RabbitTemplate rabbitTemplate) {
			return new MessagingFraudAlertNotifier(rabbitTemplate);
		}
	}
}
