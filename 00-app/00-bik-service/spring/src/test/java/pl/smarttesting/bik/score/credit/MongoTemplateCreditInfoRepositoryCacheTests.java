package pl.smarttesting.bik.score.credit;

import java.math.BigDecimal;

import io.mongock.driver.mongodb.springdata.v4.config.SpringDataMongoV4Context;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.BDDAssertions.then;

// Dotyczy lekcji 03-05
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = MongoTemplateCreditInfoRepositoryCacheTests.Config.class)
@DataMongoTest(properties = "credit.rabbit.enabled=false")
@AutoConfigureCache(cacheProvider = CacheType.SIMPLE)
@Testcontainers
class MongoTemplateCreditInfoRepositoryCacheTests {

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
	void should_retrieve_credit_info_from_cache(@Autowired CreditInfoRepository creditInfoRepository, @Autowired MongoTemplate mongoTemplate) {
		CreditInfo creditInfo = getCreditInfo(creditInfoRepository);

		then(creditInfo).as("Data should be retrieved from the database").isNotNull();

		dropCollectionCreditInfoDocuments(mongoTemplate);

		then(getCreditInfo(creditInfoRepository)).as("Data should be retrieved from the cache").isNotNull();
	}

	@Test
	void should_put_credit_info_to_cache(@Autowired CreditInfoRepository creditInfoRepository, @Autowired MongoTemplate mongoTemplate) {
		Pesel testPesel = new Pesel("09876543210");

		creditInfoRepository.saveCreditInfo(testPesel, creditInfo());

		removeTestEntryFromDatabase(mongoTemplate, testPesel);

		CreditInfo creditInfo = creditInfoRepository.findCreditInfo(testPesel);

		then(creditInfo).isNotNull();
	}

	private void removeTestEntryFromDatabase(MongoTemplate mongoTemplate, Pesel testPesel) {
		mongoTemplate.remove(peselQuery(testPesel), CreditInfoDocument.class);
		then(creditInfoExistsForPesel(mongoTemplate, testPesel)).as("Test entry should be removed from db").isFalse();
	}

	private CreditInfo creditInfo() {
		return new CreditInfo(BigDecimal.ONE, BigDecimal.ZERO, CreditInfo.DebtPaymentHistory.INDIVIDUAL_UNPAID_INSTALLMENTS);
	}

	private boolean creditInfoExistsForPesel(MongoTemplate mongoTemplate, Pesel testPesel) {
		Query query = peselQuery(testPesel);
		return mongoTemplate.exists(query, CreditInfoDocument.class);
	}

	private Query peselQuery(Pesel testPesel) {
		Query query = new Query();
		query.addCriteria(Criteria.where("pesel").is(testPesel));
		return query;
	}

	private CreditInfo getCreditInfo(@Autowired CreditInfoRepository creditInfoRepository) {
		return creditInfoRepository.findCreditInfo(new Pesel("89050193724"));
	}

	private void dropCollectionCreditInfoDocuments(MongoTemplate mongoTemplate) {
		then(mongoTemplate.collectionExists(CreditInfoDocument.class)).isTrue();
		mongoTemplate.dropCollection(CreditInfoDocument.class);
		then(mongoTemplate.collectionExists(CreditInfoDocument.class)).isFalse();
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableCaching
	@Import(CreditConfiguration.class)
	@ImportAutoConfiguration(SpringDataMongoV4Context.class)
	static class Config {

	}
}

