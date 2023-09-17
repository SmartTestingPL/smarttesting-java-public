package pl.smarttesting.bik.score.cost;

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.RestTemplate;

class CostRestTemplateClient implements RestClient {

	private final RestTemplate restTemplate;
	
	private final CircuitBreakerFactory factory;

	CostRestTemplateClient(RestTemplate restTemplate, CircuitBreakerFactory factory) {
		this.restTemplate = restTemplate;
		this.factory = factory;
	}

	@Override
	public <T> T get(String url, Class<T> returnType) {
		return this.factory.create("cost").run(() -> restTemplate.getForObject(url, returnType));
	}

}
