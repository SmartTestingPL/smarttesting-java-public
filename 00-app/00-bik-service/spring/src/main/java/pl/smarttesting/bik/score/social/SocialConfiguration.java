package pl.smarttesting.bik.score.social;

import pl.smarttesting.bik.score.cost.RestClient;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class SocialConfiguration {

	@Bean
	RestTemplate socialRestTemplate() {
		return new RestTemplate();
	}

	@Configuration(proxyBeanMethods = false)
	@Profile("!dev")
	static class Config {
		@Bean
		RestClient socialRestClient(@Qualifier("socialRestTemplate") RestTemplate socialRestTemplate, CircuitBreakerFactory circuitBreakerFactory) {
			return new SocialRestTemplateClient(socialRestTemplate, circuitBreakerFactory);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@Profile("dev")
	static class DevConfig {
		@Bean
		RestClient socialRestClient() {
			return new RestClient() {
				@Override
				public <T> T get(String url, Class<T> returnType) {
					return (T) new SocialStatus(1, 2, SocialStatus.MaritalStatus.MARRIED, SocialStatus.ContractType.EMPLOYMENT_CONTRACT);
				}
			};
		}
	}

	@Bean
	SocialStatusClient socialStatusClient(@Qualifier("socialRestClient") RestClient socialRestClient,
			@Value("${social.url:http://localhost:4567}") String incomeUrl) {
		return new SocialStatusClient(socialRestClient, incomeUrl);
	}

	@Bean
	SocialStatusScoreEvaluation socialStatusScoreEvaluation(SocialStatusClient socialStatusClient) {
		return new SocialStatusScoreEvaluation(socialStatusClient);
	}
}
