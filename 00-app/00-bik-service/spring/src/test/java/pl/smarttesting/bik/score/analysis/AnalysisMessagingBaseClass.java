package pl.smarttesting.bik.score.analysis;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Nullable;

import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.bik.score.domain.Pesel;
import pl.smarttesting.bik.score.domain.Score;
import pl.smarttesting.bik.score.domain.ScoreCalculatedEvent;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
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
@ContextConfiguration(classes = AnalysisMessagingBaseClass.TestConfig.class)
@TestPropertySource(properties = "stubrunner.amqp.mockConnection=false")
@Testcontainers
@AutoConfigureMessageVerifier
public class AnalysisMessagingBaseClass {

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
	RabbitCreditScoreUpdater scoreUpdater;

	public void triggerCreditScore() {
		this.scoreUpdater.scoreCalculated(new ScoreCalculatedEvent(new Pesel("12345678901"), new Score(100)));
	}

	@TestConfiguration(proxyBeanMethods = false)
	@Import(AnalysisConfiguration.RabbitScoreConfig.class)
	@ImportAutoConfiguration(RabbitAutoConfiguration.class)
	static class TestConfig {

		@Bean
		ScoreUpdater testRabbitCreditScoreUpdater(RabbitTemplate rabbitTemplate) {
			return new RabbitCreditScoreUpdater(rabbitTemplate);
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

		@RabbitListener(id = "score", queues = "scoreQueue")
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
