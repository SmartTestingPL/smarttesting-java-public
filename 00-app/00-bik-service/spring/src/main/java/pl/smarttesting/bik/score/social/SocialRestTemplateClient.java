package pl.smarttesting.bik.score.social;

import java.net.URI;

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import pl.smarttesting.bik.score.cost.RestClient;

class SocialRestTemplateClient implements RestClient {

	private final RestTemplate restTemplate;
	
	private final CircuitBreakerFactory factory;

	SocialRestTemplateClient(RestTemplate restTemplate, CircuitBreakerFactory factory) {
		this.restTemplate = restTemplate;
		this.factory = factory;
	}

	@Override
	public <T> T get(String url, Class<T> returnType) {
		return this.factory.create("social").run(() -> restTemplate.exchange(RequestEntity.get(URI.create(url))
				.header("Content-Type", "application/xml").build(), returnType).getBody());
	}

}
