package pl.smarttesting.verifier.customer;

import java.util.Collections;
import java.util.Set;

import pl.smarttesting.verifier.Verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Schemat konfiguracyjny dla modułu klienta.
 */
@Configuration(proxyBeanMethods = false)
class CustomerConfiguration {

	@Bean
	BIKVerificationService bikVerificationService(@Value("${bik.url:http://example.org}") String url) {
		return new BIKVerificationService(url);
	}

	/**
	 * Serwis aplikacyjny CustomerVerifier.
	 *
	 * @param service- serwis do komunikacji z BIK
	 * @param verifications - implementacje weryfikacyjne klienta. Może być {@code null} jeśli nie zdefiniowano żadnej.
	 * @param verificationRepository - repozytorium do połączenia z bazą danych
	 * @return serwis aplikacyjny
	 */
	@Bean
	CustomerVerifier customerVerifier(BIKVerificationService service, @Autowired(required = false) Set<Verification> verifications, VerificationRepository verificationRepository) {
		return new CustomerVerifier(service, verifications != null ? verifications : Collections.emptySet(), verificationRepository);
	}
}
