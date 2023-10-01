package pl.smarttesting.verifier.customer;

import java.util.Collections;
import java.util.Set;

import pl.smarttesting.verifier.Verification;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Schemat konfiguracyjny, w którym definiujemy konfigurację
 * infrastruktury - w naszym przypadku brokera RabbitMQ.
 *
 * Exchange o nazwie fraudOutput bindujemy z queue fraudOutput z routing key równym #
 * Exchange o nazwie fraudInput bindujemy z queue fraudInput z routing key równym #
 *
 * Definiujemy też komponenty biznesowe.
 */
@Configuration(proxyBeanMethods = false)
class CustomerConfiguration {

	@Configuration(proxyBeanMethods = false)
	static class RabbitMqConfiguration {

		@Bean
		Queue fraudOutputQueue() {
			return new Queue("fraudOutput");
		}

		@Bean
		Exchange fraudOutputExchange() {
			return new DirectExchange("fraudOutput");
		}

		@Bean
		Binding fraudOutputBinding(@Qualifier("fraudOutputQueue") Queue queue, @Qualifier("fraudOutputExchange") Exchange exchange) {
			return BindingBuilder.bind(queue).to(exchange).with("#").noargs();
		}

		@Bean
		Queue fraudInputQueue() {
			return new Queue("fraudInput");
		}

		@Bean
		Exchange fraudInputExchange() {
			return new DirectExchange("fraudInput");
		}

		@Bean
		Binding fraudInputBinding(@Qualifier("fraudInputQueue") Queue queue, @Qualifier("fraudInputExchange") Exchange exchange) {
			return BindingBuilder.bind(queue).to(exchange).with("#").noargs();
		}

		@Bean
		MessageConverter jackson2JsonMessageConverter() {
			return new Jackson2JsonMessageConverter();
		}
	}

	// Komponent nasłuchujący na wiadomości
	@Bean
	MessagingFraudListener fraudListener(VerificationRepository repository) {
		return new MessagingFraudListener(repository);
	}

	// Komponent wysyłający wiadomości
	@Bean
	MessagingFraudAlertNotifier fraudAlertNotifier(RabbitTemplate rabbitTemplate) {
		return new MessagingFraudAlertNotifier(rabbitTemplate);
	}

	// Nasz komponent biznesowy - serwis aplikacyjny
	@Bean
	CustomerVerifier customerVerifier(@Autowired(required = false) Set<Verification> verifications, VerificationRepository verificationRepository, FraudAlertNotifier fraudAlertNotifier) {
		return new CustomerVerifier(verifications != null ? verifications : Collections.emptySet(), verificationRepository, fraudAlertNotifier);
	}
}
