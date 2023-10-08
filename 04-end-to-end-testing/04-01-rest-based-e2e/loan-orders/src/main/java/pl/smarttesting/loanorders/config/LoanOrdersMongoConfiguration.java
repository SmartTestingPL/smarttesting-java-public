package pl.smarttesting.loanorders.config;

import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import pl.smarttesting.loanorders.repository.CustomerRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * Konfiguracja Mongo DB.
 */
@Configuration(proxyBeanMethods = false)
@EnableReactiveMongoRepositories(basePackageClasses = CustomerRepository.class)
public class LoanOrdersMongoConfiguration extends AbstractReactiveMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "loanordersdb";
	}

	@Override
	public void configureClientSettings(MongoClientSettings.Builder builder) {
		builder.uuidRepresentation(UuidRepresentation.STANDARD);
	}

}
