package pl.smarttesting.verifier.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Implementacja notyfikowania o oszuście. Wysyła wiadomość do brokera RabbitMQ.
 */
class MessagingFraudAlertNotifier implements FraudAlertNotifier {

	private static final Logger log = LoggerFactory.getLogger(MessagingFraudAlertNotifier.class);

	private final RabbitTemplate rabbitTemplate;

	public MessagingFraudAlertNotifier(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void fraudFound(CustomerVerification customerVerification) {
		log.info("Emitting message");
		this.rabbitTemplate.convertAndSend("fraudOutput", customerVerification);
	}
}
