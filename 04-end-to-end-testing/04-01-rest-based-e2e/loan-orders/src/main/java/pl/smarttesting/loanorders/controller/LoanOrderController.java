package pl.smarttesting.loanorders.controller;

import pl.smarttesting.loanorders.order.LoanOrder;
import pl.smarttesting.loanorders.order.LoanOrderService;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kontroler pozwalający na realizację operacji na wnioskach kredytowych.
 */
@RestController
@RequestMapping("/orders")
public class LoanOrderController {

	private final LoanOrderService loanOrderService;

	public LoanOrderController(LoanOrderService loanOrderService) {
		this.loanOrderService = loanOrderService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	Mono<String> createOrder(@RequestBody LoanOrder loanOrder) {
		return loanOrderService.verifyLoanOrder(loanOrder);
	}

	@GetMapping(path = "/{orderId}")
	Mono<LoanOrder> findOrder(@PathVariable String orderId) {
		return loanOrderService.findOrder(orderId);
	}

}
