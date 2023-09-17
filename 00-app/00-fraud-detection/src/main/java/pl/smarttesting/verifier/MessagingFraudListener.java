package pl.smarttesting.verifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Person;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Implementacja nasłuchująca na wiadomości z brokera RabbitMQ.
 */
class MessagingFraudListener implements FraudListener {

	private static final Logger log = LoggerFactory.getLogger(MessagingFraudListener.class);

	private final VerificationRepository repository;

	public MessagingFraudListener(VerificationRepository repository) {
		this.repository = repository;
	}

	/**
	 * Poprzez adnotację {@link RabbitListener}, wskazujemy kolejki, z których
	 * chcielibyśmy pobrać wiadomości.
	 *
	 * @param customerVerification - weryfikacja klienta
	 */
	@Override
	@RabbitListener(queues = "fraudInput")
	public void onFraud(CustomerVerification customerVerification) {
		log.info("Got verification [{}]", customerVerification);
		Person person = customerVerification.getPerson();
		CustomerVerificationResult result = customerVerification.getResult();
		this.repository.save(new VerifiedPerson(result.getUserId(), person.getNationalIdentificationNumber(), result.getStatus()));
	}
}
