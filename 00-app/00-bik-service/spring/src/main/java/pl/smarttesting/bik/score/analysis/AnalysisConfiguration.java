package pl.smarttesting.bik.score.analysis;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.bik.score.ScoreEvaluation;
import pl.smarttesting.bik.score.domain.ScoreCalculatedEvent;

@Configuration(proxyBeanMethods = false)
class AnalysisConfiguration {

	@Bean
	CompositeScoreEvaluation compositeScoreEvaluation(List<ScoreEvaluation> scoreEvaluations, 
			@Qualifier("bikExecutorService") ExecutorService executorService, 
			ScoreUpdater scoreUpdater) {
		return new ParallelCompositeScoreEvaluation(scoreEvaluations, executorService, scoreUpdater); 
	}
	
	@Bean(destroyMethod = "shutdown")
	ExecutorService bikExecutorService() {
		return Executors.newCachedThreadPool();
	}
	
	@Bean
	ScoreAnalyzer scoreAnalyzer(CompositeScoreEvaluation compositeScoreEvaluation, MeterRegistry meterRegistry, 
			@Value("${bik.score.threshold:500}") int threshold) {
		return new ScoreAnalyzer(compositeScoreEvaluation, meterRegistry, threshold);
	}

	@Configuration(proxyBeanMethods = false)
	@Profile("!dev")
	static class Config {

		@Bean
		ScoreUpdater rabbitCreditScoreUpdater(RabbitTemplate rabbitTemplate) {
			return new RabbitCreditScoreUpdater(rabbitTemplate);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Profile("dev")
	static class DevConfig {

		@Bean
		ScoreUpdater devCreditScoreUpdater() {
			return new ScoreUpdater() {

				private static final Logger log = LoggerFactory.getLogger(ScoreUpdater.class);

				@Override
				public void scoreCalculated(ScoreCalculatedEvent scoreCalculatedEvent) {
					log.info("Got the event {}", scoreCalculatedEvent);
				}
			};
		}

	}

	@Configuration(proxyBeanMethods = false)
	static class RabbitScoreConfig {
		
		@Bean
		@ConditionalOnMissingBean
		Jackson2JsonMessageConverter scoreMessageConverter() {
			return new Jackson2JsonMessageConverter();
		}
		
		@Bean
		Queue scoreQueue() {
			return new Queue("scoreQueue");
		}

		@Bean
		Exchange scoreExchange() {
			return new DirectExchange("scoreExchange");
		}

		@Bean
		Binding scoreBinding(@Qualifier("scoreQueue") Queue queue, @Qualifier("scoreExchange") Exchange exchange) {
			return BindingBuilder.bind(queue).to(exchange).with("#").noargs();
		}

	}
}
