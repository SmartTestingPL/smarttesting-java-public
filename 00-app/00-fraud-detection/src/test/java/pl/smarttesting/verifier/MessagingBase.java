package pl.smarttesting.verifier;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nullable;

import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.customer.Person;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessage;
import org.springframework.cloud.contract.verifier.messaging.internal.ContractVerifierMessaging;
import org.springframework.cloud.contract.verifier.messaging.noop.NoOpStubMessages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// Dotyczy lekcji 05-03
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MessagingBase.TestConfig.class)
@TestPropertySource(properties = "stubrunner.amqp.mockConnection=false")
@Testcontainers
@AutoConfigureMessageVerifier
public abstract class MessagingBase {

	@Container
	static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.8.27-management-alpine")
			.withReuse(true);

	static {
		rabbit.start();
	}

	@DynamicPropertySource
	static void rabbitProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
	}

	@Autowired
	MessagingFraudAlertNotifier messagingFraudAlertNotifier;

	public void fraudFound() {
		this.messagingFraudAlertNotifier.fraudFound(new CustomerVerification(new Person("Fraudeusz", "Fraudowski", "01-01-1980", Person.GENDER.MALE, "2345678901"), new CustomerVerificationResult(UUID.fromString("cc8aa8ff-40ff-426f-bc71-5bb7ea644108"), CustomerVerificationResult.Status.VERIFICATION_FAILED)));
	}

	@TestConfiguration(proxyBeanMethods = false)
	@Import(CustomerConfiguration.ProdCustomerConfiguration.RabbitConfiguration.class)
	@ImportAutoConfiguration(RabbitAutoConfiguration.class)
	static class TestConfig {

		@Bean
		MessagingFraudAlertNotifier testMessagingFraudAlertNotifier(RabbitTemplate rabbitTemplate) {
			return new MessagingFraudAlertNotifier(rabbitTemplate);
		}

		@Bean
		RabbitMessageVerifier rabbitTemplateMessageVerifier() {
			return new RabbitMessageVerifier();
		}

		@Bean
		ContractVerifierMessaging<Message> rabbitContractVerifierMessaging(RabbitMessageVerifier messageVerifier) {
			return new ContractVerifierMessaging<>(new NoOpStubMessages<>(), messageVerifier) {

				@Override
				protected ContractVerifierMessage convert(Message receive) {
					if (receive == null) {
						return null;
					}
					return new ContractVerifierMessage(receive.getPayload(), receive.getHeaders());
				}

			};
		}

	}

	static class RabbitMessageVerifier implements MessageVerifierReceiver<Message> {

		private static final Logger log = LoggerFactory.getLogger(RabbitMessageVerifier.class);

		private final LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

		@Override
		public Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
			try {
				return queue.poll(timeout, timeUnit);
			}
			catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}

		@RabbitListener(id = "fraudOutput", queues = "fraudOutput")
		public void listen(Message message) {
			log.info("Got a message! [{}]", message);
			queue.add(message);
		}

		@Override
		public Message receive(String destination, YamlContract contract) {
			return receive(destination, 1, TimeUnit.SECONDS, contract);
		}
	}
}
