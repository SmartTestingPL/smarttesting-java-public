package pl.smarttesting.verifier.model;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Schemat konfiguracyjny klienta.
 */
@Configuration(proxyBeanMethods = false)
class CustomerConfiguration {

	@Bean
	CustomerVerifier customerVerifier(@Autowired(required = false)Set<Verification> verifications) {
		return new CustomerVerifier(verifications != null ? verifications : Collections.emptySet());
	}
}