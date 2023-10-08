package pl.smarttesting.loanorders.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.loanorders.customer.Customer;
import pl.smarttesting.loanorders.fraud.CustomerVerificationResult;
import pl.smarttesting.loanorders.fraud.FraudWebClient;
import pl.smarttesting.loanorders.repository.LoanOrderRepository;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;


/**
 * Serwis do realizacji operacji na wnioskach kredytowych.
 */
@Service
public class LoanOrderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanOrderService.class);
	private final LoanOrderRepository loanOrderRepository;
	private final FraudWebClient fraudWebClient;

	public LoanOrderService(LoanOrderRepository loanOrderRepository,
			FraudWebClient fraudWebClient) {
		this.loanOrderRepository = loanOrderRepository;
		this.fraudWebClient = fraudWebClient;
	}

	public Mono<String> verifyLoanOrder(LoanOrder loanOrder) {
		Customer customer = loanOrder.getCustomer();
		Mono<LoanOrder> verifiedLoanOrder = fraudWebClient.verifyCustomer(customer)
				.map(customerVerificationResult -> {
					if (CustomerVerificationResult.Status.VERIFICATION_FAILED
							.equals(customerVerificationResult.getStatus())) {
						LOGGER.warn("Customer {} has not passed verification", customer
								.getUuid());
						return updateStatus(loanOrder, LoanOrder.Status.REJECTED);
					}
					return updateStatus(loanOrder, LoanOrder.Status.VERIFIED);
				});
		return verifiedLoanOrder.flatMap(order -> loanOrderRepository.save(order)
				.map(LoanOrder::getId));
	}

	public Mono<LoanOrder> findOrder(String orderId) {
		return loanOrderRepository.findById(orderId);
	}

	private LoanOrder updateStatus(LoanOrder loanOrder, LoanOrder.Status status) {
		loanOrder.setStatus(status);
		return loanOrder;
	}
}