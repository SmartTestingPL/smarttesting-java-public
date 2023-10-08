package pl.smarttesting.fraudverifier.controller;

import pl.smarttesting.fraudverifier.customer.Customer;
import pl.smarttesting.fraudverifier.customer.CustomerVerificationResult;
import pl.smarttesting.fraudverifier.customer.CustomerVerifier;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kontroler umożliwiający wykonywanie operacji na klientach.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerVerifier customerVerifier;

	public CustomerController(CustomerVerifier customerVerifier) {
		this.customerVerifier = customerVerifier;
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	Mono<CustomerVerificationResult> verify(@RequestBody Customer customer) {
		return Mono.defer(() -> Mono.just(customerVerifier.verify(customer)));
	}
}
