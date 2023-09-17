package pl.smarttesting.bik.score.income;

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.RestTemplate;

import pl.smarttesting.bik.score.cost.RestClient;

class IncomeRestTemplateClient implements RestClient {

	private final RestTemplate restTemplate;
	
	private final CircuitBreakerFactory factory;

	IncomeRestTemplateClient(RestTemplate restTemplate, CircuitBreakerFactory factory) {
		this.restTemplate = restTemplate;
		this.factory = factory;
	}

	@Override
	public <T> T get(String url, Class<T> returnType) {
		return this.factory.create("income").run(() -> restTemplate.getForObject(url, returnType));
	}

}
