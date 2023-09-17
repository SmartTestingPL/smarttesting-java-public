package pl.smarttesting.bik.score.credit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.bik.score.domain.Pesel;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!dev")
class MongoTemplateCreditInfoRepository implements CreditInfoRepository {

	private static final Logger log = LoggerFactory.getLogger(MongoTemplateCreditInfoRepository.class);
	
	private final MongoTemplate mongoTemplate;
	
	public MongoTemplateCreditInfoRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	@Cacheable(cacheNames = "credit-info")
	public CreditInfo findCreditInfo(Pesel pesel) {
		log.info("Getting credit info from Mongo for [{}]", pesel);
		Query query = new Query();
		query.addCriteria(Criteria.where("pesel").is(pesel));
		CreditInfoDocument creditInfoDocument = this.mongoTemplate.findOne(query, CreditInfoDocument.class);
		log.info("Found credit info [{}] for [{}]", creditInfoDocument, pesel);
		if (creditInfoDocument == null) {
			return null;
		}
		return creditInfoDocument.getCreditInfo();
	}

	@Override
	@CachePut(cacheNames = "credit-info", key = "#pesel")
	public CreditInfo saveCreditInfo(Pesel pesel, CreditInfo creditInfo) {
		log.info("Storing credit info [{}] for [{}]", creditInfo, pesel);
		this.mongoTemplate.save(new CreditInfoDocument(creditInfo, pesel));
		return creditInfo;
	}

}
