package pl.smarttesting.bik.score.personal;

import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

import pl.smarttesting.bik.score.cost.RestClient;
import pl.smarttesting.bik.score.domain.Score;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class PersonalConfiguration {

	@Bean(name = "personalRestTemplate")
	RestTemplate personalRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	PersonalInformationClient personalInformationClient(@Qualifier("personalRestClient") RestClient personalRestClient, 
			@Value("${personal.url:http://localhost:2345}") String personalUrl) {
		return new PersonalInformationClient(personalRestClient, personalUrl);
	}

	@Configuration(proxyBeanMethods = false)
	@Profile("!dev")
	static class Config {
		@Bean
		OccupationRepository occupationRepository(DSLContext dslContext) {
			return new JooqOccupationRepository(dslContext);
		}

		@Bean
		RestClient personalRestClient(@Qualifier("personalRestTemplate") RestTemplate incomeRestTemplate, CircuitBreakerFactory circuitBreakerFactory) {
			return new PersonalRestTemplateClient(incomeRestTemplate, circuitBreakerFactory);
		}
	}

	@Configuration(proxyBeanMethods = false)
	@Profile("dev")
	static class DevConfig {
		@Bean
		OccupationRepository occupationRepository() {
			return () -> Map.of(PersonalInformation.Occupation.DOCTOR, new Score(100));
		}

		@Bean
		RestClient personalRestClient() {
			return new RestClient() {
				@Override
				public <T> T get(String url, Class<T> returnType) {
					return (T) new PersonalInformation(PersonalInformation.Education.BASIC, 10, PersonalInformation.Occupation.DOCTOR);
				}
			};
		}
	}


	@Bean
	PersonalInformationScoreEvaluation personalInformationScoreEvaluation(PersonalInformationClient personalInformationClient, 
			OccupationRepository occupationRepository) {
		return new PersonalInformationScoreEvaluation(personalInformationClient, occupationRepository);
	}
}
