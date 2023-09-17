package pl.smarttesting.bik.score.personal;

import static org.jooq.impl.DSL.table;

import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import pl.smarttesting.bik.score.domain.Score;

@Repository
@Profile("!dev")
class JooqOccupationRepository implements OccupationRepository {

	private static final Logger log = LoggerFactory.getLogger(JooqOccupationRepository.class);
	
	private final DSLContext dsl;

	public JooqOccupationRepository(DSLContext dsl) {
		this.dsl = dsl;
	}

	@Override
	@Cacheable(cacheNames = "occupation-scores")
	public Map<PersonalInformation.Occupation, Score> getOccupationScores() {
		Result<Record> fetch = this.dsl.select()
				.from(table("occupation_to_score"))
				.fetch();
		log.info("Fetched the following occupation scores\n {}", fetch);
		return fetch.stream().collect(Collectors.toMap(
				r -> PersonalInformation.Occupation.valueOf(r.get("occupation").toString()), 
				r -> new Score(Integer.parseInt(r.get("occupation_score").toString()))));
	}
}
