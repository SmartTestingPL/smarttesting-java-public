package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.BDDAssertions.then;

// Dotyczy lekcji 03-05
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = MongoTemplateCreditInfoRepositoryNoCacheTests.Config.class)
@DataMongoTest(properties = "credit.rabbit.enabled=false")
@AutoConfigureCache(cacheProvider = CacheType.NONE)
@Testcontainers
class MongoTemplateCreditInfoRepositoryNoCacheTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.4")
			.withReuse(true);

	static {
		mongoDBContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void should_retrieve_credit_info_from_db(@Autowired CreditInfoRepository creditInfoRepository, @Autowired MongoTemplate mongoTemplate) {
		Pesel testPesel = new Pesel("09876543210");
		mongoTemplate.save(new CreditInfoDocument(creditInfo(), testPesel));
		then(creditInfoExistsForPesel(mongoTemplate, testPesel)).as("There should be a test entry before running the test").isTrue();

		CreditInfo creditInfo = creditInfoRepository.findCreditInfo(testPesel);

		then(creditInfo).isNotNull();
	}

	@Test
	void should_save_credit_to_db(@Autowired CreditInfoRepository creditInfoRepository, @Autowired MongoTemplate mongoTemplate) {
		Pesel testPesel = new Pesel("12345678901");
		then(creditInfoExistsForPesel(mongoTemplate, testPesel)).as("There should not be a test entry before running the test").isFalse();

		creditInfoRepository.saveCreditInfo(testPesel, creditInfo());

		then(creditInfoExistsForPesel(mongoTemplate, testPesel)).as("There should be a test entry after running the test").isTrue();
	}

	private CreditInfo creditInfo() {
		return new CreditInfo(BigDecimal.ONE, BigDecimal.ZERO, CreditInfo.DebtPaymentHistory.INDIVIDUAL_UNPAID_INSTALLMENTS);
	}

	private boolean creditInfoExistsForPesel(MongoTemplate mongoTemplate, Pesel testPesel) {
		Query query = new Query();
		query.addCriteria(Criteria.where("pesel").is(testPesel));
		return mongoTemplate.exists(query, CreditInfoDocument.class);
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableMongoRepositories
	static class Config {
		@Bean
		CreditInfoRepository creditInfoRepository(MongoTemplate mongoTemplate) {
			return new MongoTemplateCreditInfoRepository(mongoTemplate);
		}
	}
}

