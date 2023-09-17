package pl.smarttesting.bik.score.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import pl.smarttesting.bik.score.domain.ScoreCalculatedEvent;

class RabbitCreditScoreUpdater implements ScoreUpdater {

	private static final Logger log = LoggerFactory.getLogger(RabbitCreditScoreUpdater.class);
	
	private final RabbitTemplate rabbitTemplate;
	
	public RabbitCreditScoreUpdater(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void scoreCalculated(ScoreCalculatedEvent scoreCalculatedEvent) {
		log.info("Sending out the score calculated event {}", scoreCalculatedEvent);
		this.rabbitTemplate.convertAndSend("scoreExchange", "#", scoreCalculatedEvent);
	}

}
