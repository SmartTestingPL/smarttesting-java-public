package pl.smarttesting.verifier;

import java.util.Collections;
import java.util.Set;

import pl.smarttesting.customer.Customer;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Schemat konfiguracyjny naszej aplikacji. Posiada podschematy:
 *
 * Schemat domyślny (niezależny od profilu), zdefiniowany w CustomerConfiguration
 * Schemat deweloperski (uruchomiony poprzez profil dev - -Dspring.profiles.active=dev), zdefiniowany w DevCustomerConfiguration
 * Schemat produkcyjny (każdy inny profil tylko nie dev), zdefiniowany w ProdCustomerConfiguration
 */
@Configuration(proxyBeanMethods = false)
class CustomerConfiguration {

	@Bean
	CustomerVerifier customerVerifier(BIKVerificationService service, @Autowired(required = false)Set<Verification> verifications, VerificationRepository verificationRepository, FraudAlertNotifier fraudAlertNotifier) {
		return new CustomerVerifier(service, verifications != null ? verifications : Collections.emptySet(), verificationRepository, fraudAlertNotifier);
	}

	@Bean
	MessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}


	/**
	 * Konfiguracja produkcyjna, aktywowana, gdy podano jakikolwiek inny profil niż dev.
	 * W tym schemacie konfiguracyjnym mamy prawdziwe połączenie do BIK, do brokera, bazy danych.
	 */
	@Profile("!dev")
	static class ProdCustomerConfiguration {
		@Bean
		BIKVerificationService bikVerificationService(@Value("${bik.url:http://example.org}") String url) {
			return new BIKVerificationService(url);
		}

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
		FraudListener fraudListener(VerificationRepository repository) {
			return new MessagingFraudListener(repository);
		}

		@Bean
		FraudAlertNotifier fraudAlertNotifier(RabbitTemplate rabbitTemplate) {
			return new MessagingFraudAlertNotifier(rabbitTemplate);
		}
	}

	/**
	 * Konfiguracja deweloperska, aktywowana, gdy podano profil dev.
	 * W tym schemacie konfiguracyjnym połączenie do BIK jest zaślepione, podmienione zostają
	 * wszystkie komponenty, które biorą udział w integracji z usługami zewnętrznymi.
	 */
	@Profile("dev")
	static class DevCustomerConfiguration {
		@Bean
		BIKVerificationService mockedBikVerificationService() {
			return new BIKVerificationService("") {
				@Override
				public CustomerVerificationResult verify(Customer customer) {
					if (customer.getPerson().getName().equals("Fraudeusz")) {
						return CustomerVerificationResult.failed(customer.getUuid());
					}
					return CustomerVerificationResult.passed(customer.getUuid());
				}
			};
		}

		@Bean
		FraudListener fraudListener(VerificationRepository repository) {
			return new DevFraudListener(new MessagingFraudListener(repository));
		}

		@Bean
		FraudAlertNotifier fraudAlertNotifier() {
			return new DevFraudAlertNotifier();
		}

		@Bean
		DevFraudRepositoryAccessor devFraudRepositoryAccessor(VerificationRepository repository) {
			return new DevFraudRepositoryAccessor(repository);
		}
	}
}