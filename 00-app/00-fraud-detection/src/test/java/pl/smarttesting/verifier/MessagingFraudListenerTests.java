package pl.smarttesting.verifier;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;

// Dotyczy lekcji 03-06
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagingFraudListenerTests.TestConfig.class)
@Testcontainers
class MessagingFraudListenerTests {

	@Container
	private static final RabbitMQContainer CONTAINER = new RabbitMQContainer("rabbitmq:3.8.27-management-alpine")
			.withReuse(true);

	static {
		CONTAINER.start();
	}

	@DynamicPropertySource
	static void rabbitProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.port", CONTAINER::getAmqpPort);
	}

	@MockBean VerificationRepository repository;

	@Autowired RabbitTemplate rabbitTemplate;

	@Test
	void should_store_a_fraud_when_a_customer_verification_was_received_from_an_external_system() {
		Customer customer = stefania();

		rabbitTemplate.convertAndSend("fraudInput", "#", failedStefaniaVerification(customer));

		await().untilAsserted(() -> repository.save(argThat(person -> Objects.equals(person.getUserId(), customer.getUuid()))));
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

	@TestConfiguration
	@Import(CustomerConfiguration.ProdCustomerConfiguration.RabbitConfiguration.class)
	@ImportAutoConfiguration(RabbitAutoConfiguration.class)
	static class TestConfig {

		@Bean
		MessagingFraudListener testFraudAlertNotifier(VerificationRepository verificationRepository) {
			return new MessagingFraudListener(verificationRepository);
		}
	}
}
