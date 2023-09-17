package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.then;

// Dotyczy lekcji 03-06
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RabbitCreditInfoListenerTests.TestConfig.class)
@Testcontainers
class RabbitCreditInfoListenerTests {

	@Container
	static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.8.27-management-alpine")
			.withReuse(true);

	static {
		rabbit.start();
	}

	@DynamicPropertySource
	static void rabbitProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
	}

	@MockBean
	CreditInfoRepository scoreUpdater;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	void should_store_credit_info_when_message_received() {
		rabbitTemplate.send("creditInfo", "#", MessageBuilder.withBody("""
				{
				  "creditInfo" : {
				    "currentDebt" : 1000,
				    "currentLivingCosts" : 2000,
				    "debtPaymentHistory" : "NOT_A_SINGLE_UNPAID_INSTALLMENT"
				   },
				  "pesel" : {
				    "pesel" : "49111144777"
				  }
				}
				""".getBytes(StandardCharsets.UTF_8)).setHeader("contentType", "application/json").build());

		await().untilAsserted(() -> then(scoreUpdater).should().saveCreditInfo(new Pesel("49111144777"), new CreditInfo(new BigDecimal("1000"), new BigDecimal("2000"), CreditInfo.DebtPaymentHistory.NOT_A_SINGLE_UNPAID_INSTALLMENT)));
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableRabbit
	@ImportAutoConfiguration(RabbitAutoConfiguration.class)
	@Import(CreditConfiguration.RabbitCreditConfig.class)
	static class TestConfig {

		@Bean
		CreditInfoListener testRabbitCreditInfoListener(CreditInfoRepository creditInfoRepository) {
			return new RabbitCreditInfoListener(creditInfoRepository);
		}
	}

}
