package pl.smarttesting.loanorders.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Konfiguracja RestTemplate.
 */
@Configuration(proxyBeanMethods = false)
public class RestTemplateConfig {

	@LoadBalanced
	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
