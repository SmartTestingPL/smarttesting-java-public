package pl.smarttesting.bik.score.income;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import pl.smarttesting.bik.score.cost.RestClient;

@Configuration(proxyBeanMethods = false)
public class IncomeConfiguration {

	@Bean
	RestTemplate incomeRestTemplate() {
		return new RestTemplate();
	}

	@Configuration(proxyBeanMethods = false)
	@Profile("!dev")
	static class Config {
		@Bean
		RestClient incomeRestClient(@Qualifier("incomeRestTemplate") RestTemplate incomeRestTemplate, CircuitBreakerFactory circuitBreakerFactory) {
			return new IncomeRestTemplateClient(incomeRestTemplate, circuitBreakerFactory);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Profile("dev")
	static class DevConfig {
		@Bean
		RestClient incomeRestClient() {
			return new RestClient() {
				@Override
				public <T> T get(String url, Class<T> returnType) {
					return (T) "2000";
				}
			};
		}
	}

	@Bean
	MonthlyIncomeClient monthlyIncomeClient(@Qualifier("incomeRestClient") RestClient incomeRestClient, 
			@Value("${monthly-income.url:http://localhost:1234}") String incomeUrl) {
		return new MonthlyIncomeClient(incomeRestClient, incomeUrl);
	}

	@Bean
	MonthlyIncomeScoreEvaluation monthlyIncomeScoreEvaluation(MonthlyIncomeClient monthlyIncomeClient) {
		return new MonthlyIncomeScoreEvaluation(monthlyIncomeClient);
	}
}
