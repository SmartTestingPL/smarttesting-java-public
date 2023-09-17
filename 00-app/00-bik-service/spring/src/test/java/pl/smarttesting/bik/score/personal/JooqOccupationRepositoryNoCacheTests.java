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
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.BDDAssertions.then;

// Dotyczy lekcji 03-05
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = JooqOccupationRepositoryNoCacheTests.Config.class)
@JooqTest(properties = {
		"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
		"spring.datasource.url=jdbc:tc:postgresql:11.1:///integration-tests-db",
		"spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL10Dialect"})
@AutoConfigureCache(cacheProvider = CacheType.NONE)
class JooqOccupationRepositoryNoCacheTests {

	@Test
	void should_retrieve_occupation_scores(@Autowired OccupationRepository occupationRepository) {
		Map<PersonalInformation.Occupation, Score> occupationScores = occupationRepository.getOccupationScores();

		then(occupationScores).containsOnlyKeys(PersonalInformation.Occupation.values());
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class Config {
		@Bean
		OccupationRepository testOccupationRepository(DSLContext dslContext) {
			return new JooqOccupationRepository(dslContext);
		}
	}
}

