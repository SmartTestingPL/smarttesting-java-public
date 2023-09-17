package pl.smarttesting.bik.score.credit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import pl.smarttesting.bik.score.domain.Pesel;

class RabbitCreditInfoListener implements CreditInfoListener {

	private static final Logger log = LoggerFactory.getLogger(RabbitCreditInfoListener.class);	
	
	private final CreditInfoRepository repository;
	
	public RabbitCreditInfoListener(CreditInfoRepository repository) {
		this.repository = repository;
	}

	@Override
	public void storeCreditInfo(Pesel pesel, CreditInfo creditInfo) {
		this.repository.saveCreditInfo(pesel, creditInfo);	
	}
	
	@RabbitListener(queues = "creditInfo")
	public void onMessage(CreditInfoDocument creditInfoDocument) {
		log.info("Got message from credit info queue [{}]", creditInfoDocument);
		storeCreditInfo(creditInfoDocument.getPesel(), creditInfoDocument.getCreditInfo());
	}

}
