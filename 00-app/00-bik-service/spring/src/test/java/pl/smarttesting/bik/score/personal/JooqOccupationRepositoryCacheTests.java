package pl.smarttesting.bik.score.personal;

import java.util.Map;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import pl.smarttesting.bik.score.domain.Score;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.BDDAssertions.then;

// Dotyczy lekcji 03-05
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = JooqOccupationRepositoryCacheTests.Config.class)
@JooqTest(properties = {
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-db-cache",
		"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect"})
@AutoConfigureCache(cacheProvider = CacheType.SIMPLE)
class JooqOccupationRepositoryCacheTests {

	@Test
	void should_retrieve_occupation_scores_from_cache(@Autowired OccupationRepository occupationRepository, @Autowired DSLContext dslContext) {
		Map<PersonalInformation.Occupation, Score> occupationScores = getOccupationScores(occupationRepository);

		then(occupationScores).as("Data should be retrieved from the database").containsOnlyKeys(PersonalInformation.Occupation.values());

		dropOccupationToScoreTable(dslContext);

		then(getOccupationScores(occupationRepository)).as("Data should be retrieved from the cache").containsOnlyKeys(PersonalInformation.Occupation.values());
	}

	private Map<PersonalInformation.Occupation, Score> getOccupationScores(@Autowired OccupationRepository occupationRepository) {
		return occupationRepository.getOccupationScores();
	}

	private void dropOccupationToScoreTable(DSLContext dslContext) {
		dslContext.dropTable("occupation_to_score").execute();
		then(dslContext.meta().getTables("occupation_to_score")).isEmpty();
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableCaching
	static class Config {
		@Bean
		OccupationRepository testOccupationRepository(DSLContext dslContext) {
			return new JooqOccupationRepository(dslContext);
		}
	}
}

