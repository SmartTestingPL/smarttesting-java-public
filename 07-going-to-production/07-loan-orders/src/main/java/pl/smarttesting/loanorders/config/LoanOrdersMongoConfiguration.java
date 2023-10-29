package pl.smarttesting.loanorders.config;

import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import pl.smarttesting.loanorders.repository.CustomerRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Konfiguracja Mongo DB.
 */
@Configuration(proxyBeanMethods = false)
@EnableMongoRepositories(basePackageClasses = CustomerRepository.class)
public class LoanOrdersMongoConfiguration extends AbstractMongoClientConfiguration {

	@Override
	protected String getDatabaseName() {
		return "loanordersdb";
	}

	@Override
	public void configureClientSettings(MongoClientSettings.Builder builder) {
		builder.uuidRepresentation(UuidRepresentation.STANDARD);
	}

}
