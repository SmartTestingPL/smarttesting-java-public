package pl.smarttesting.loanorders.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.loanorders.customer.Customer;
import pl.smarttesting.loanorders.fraud.CustomerVerificationResult;
import pl.smarttesting.loanorders.fraud.FraudWebClient;
import pl.smarttesting.loanorders.repository.LoanOrderRepository;

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

	public String verifyLoanOrder(LoanOrder loanOrder) {
		Customer customer = loanOrder.getCustomer();
		CustomerVerificationResult verificationResult = fraudWebClient
				.verifyCustomer(customer)
				.getBody();
		if (verificationResult == null || CustomerVerificationResult.Status.VERIFICATION_FAILED
				.equals(verificationResult.getStatus())) {
			LOGGER.warn("Customer {} has not passed verification", customer
					.getUuid());
			loanOrder.setStatus(LoanOrder.Status.REJECTED);
		}
		else {
			loanOrder.setStatus(LoanOrder.Status.VERIFIED);
		}
		return loanOrderRepository.save(loanOrder).getId();
	}

	public LoanOrder findOrder(String orderId) {
		return loanOrderRepository.findById(orderId).orElseGet(() -> {
			LOGGER.warn("LoanOrder with id {} could not be found in the database.", orderId);
			return null;
		});
	}
}