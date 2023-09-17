package pl.smarttesting.verifier;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.BDDAssertions.then;

// Dotyczy lekcji 03-06
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagingFraudAlertNotifierTests.TestConfig.class)
@Testcontainers
class MessagingFraudAlertNotifierTests {

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

	@TestConfiguration
	@Import(CustomerConfiguration.ProdCustomerConfiguration.RabbitConfiguration.class)
	@ImportAutoConfiguration(RabbitAutoConfiguration.class)
	static class TestConfig {

		@Bean
		FraudAlertNotifier testFraudAlertNotifier(RabbitTemplate rabbitTemplate) {
			return new MessagingFraudAlertNotifier(rabbitTemplate);
		}
	}
}
