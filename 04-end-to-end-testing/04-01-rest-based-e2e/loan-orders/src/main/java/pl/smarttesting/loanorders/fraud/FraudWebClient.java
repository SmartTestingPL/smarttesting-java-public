package pl.smarttesting.loanorders.fraud;

import pl.smarttesting.loanorders.customer.Customer;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Klient do komunikacji z serwisem Fraud.
 */
@Component
public class FraudWebClient {

	private final WebClient webClient;

	public FraudWebClient(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder
				.baseUrl("http://fraud-verifier")
				.build();
	}


	public Mono<CustomerVerificationResult> verifyCustomer(Customer customer) {
		return webClient.post()
				.uri(uriBuilder -> uriBuilder.pathSegment("customers").build())
				.bodyValue(customer)
				.exchange()
				.flatMap(clientResponse -> clientResponse
						.bodyToMono(CustomerVerificationResult.class));
	}
}
