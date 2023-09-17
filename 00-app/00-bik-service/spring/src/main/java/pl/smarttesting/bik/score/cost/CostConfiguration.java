package pl.smarttesting.bik.score.cost;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class CostConfiguration {

	@Bean
	RestTemplate monthlyCostRestTemplate() {
		return new RestTemplate();
	}

	@Configuration(proxyBeanMethods = false)
	@Profile("!dev")
	static class Config {
		@Bean
		RestClient monthlyCostRestTemplateClient(@Qualifier("monthlyCostRestTemplate") RestTemplate monthlyCostRestTemplate, CircuitBreakerFactory circuitBreakerFactory) {
			return new CostRestTemplateClient(monthlyCostRestTemplate, circuitBreakerFactory);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Profile("dev")
	static class DevConfig {
		@Bean
		RestClient monthlyCostRestTemplateClient() {
			return new RestClient() {
				@Override
				public <T> T get(String url, Class<T> returnType) {
					return (T) "1000";
				}
			};
		}
	}

	@Bean
	MonthlyCostClient monthlyCostClient(@Qualifier("monthlyCostRestTemplateClient") RestClient monthlyCostRestTemplateClient, 
			@Value("${monthly-cost.url:http://localhost:3456}") String costUrl) {
		return new MonthlyCostClient(monthlyCostRestTemplateClient, costUrl);
	}

	@Bean 
	MonthlyCostScoreEvaluation monthlyCostScoreEvaluation(MonthlyCostClient monthlyCostClient) {
		return new MonthlyCostScoreEvaluation(monthlyCostClient);
	}
	
}
