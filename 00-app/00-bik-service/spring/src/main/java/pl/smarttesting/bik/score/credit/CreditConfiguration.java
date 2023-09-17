package pl.smarttesting.bik.score.credit;

import java.util.concurrent.ConcurrentHashMap;

import io.mongock.runner.springboot.EnableMongock;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration(proxyBeanMethods = false)
@EnableMongoRepositories
@EnableMongock
public class CreditConfiguration {

	@Configuration(proxyBeanMethods = false)
	@Profile("!dev")
	static class Config {

		@Bean
		CreditInfoRepository creditInfoRepository(MongoTemplate mongoTemplate) {
			return new MongoTemplateCreditInfoRepository(mongoTemplate);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Profile("dev")
	static class DevConfig {

		@Bean
		CreditInfoRepository creditInfoRepository() {
			return new CreditInfoRepository() {

				private final ConcurrentHashMap<Pesel, CreditInfo> db = new ConcurrentHashMap<>();

				@Override
				public CreditInfo findCreditInfo(Pesel pesel) {
					return db.get(pesel);
				}

				@Override
				public CreditInfo saveCreditInfo(Pesel pesel, CreditInfo creditInfo) {
					return db.put(pesel, creditInfo);
				}
			};
		}

	}

	@Bean
	CreditInfoScoreEvaluation creditInfoScoreEvaluation(CreditInfoRepository creditInfoRepository) {
		return new CreditInfoScoreEvaluation(creditInfoRepository);
	}
	
	@Bean
	CreditInfoListener rabbitCreditInfoListener(CreditInfoRepository creditInfoRepository) {
		return new RabbitCreditInfoListener(creditInfoRepository);
	}
	
	@Configuration(proxyBeanMethods = false)
	// for testing
	@ConditionalOnProperty(value = "credit.rabbit.enabled", matchIfMissing = true)
	static class RabbitCreditConfig {
		
		@Bean
		@ConditionalOnMissingBean
		Jackson2JsonMessageConverter creditMessageConverter() {
			return new Jackson2JsonMessageConverter();
		}
		
		@Bean
		Queue creditInfoQueue() {
			return new Queue("creditInfo");
		}

		@Bean
		Exchange creditInfoExchange() {
			return new DirectExchange("creditInfo");
		}

		@Bean
		Binding creditInfoBinding(@Qualifier("creditInfoQueue") Queue queue, @Qualifier("creditInfoExchange") Exchange exchange) {
			return BindingBuilder.bind(queue).to(exchange).with("#").noargs();
		}

	}
}
