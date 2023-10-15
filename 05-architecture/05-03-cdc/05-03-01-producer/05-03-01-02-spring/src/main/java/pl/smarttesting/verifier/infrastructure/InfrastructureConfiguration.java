package pl.smarttesting.verifier.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Schemat konfiguracyjny infrastruktury.
 */
@Configuration(proxyBeanMethods = false)
class InfrastructureConfiguration {
	/**
	 * Komponent wykorzystujący {@link JdbcTemplate} do wykonania kwerend bazodanowych.
	 *
	 * @param jdbcTemplate - komponent do połączenia z bazą danych
	 * @return rezpoytorium do łączenia się z bazą danych
	 */
	@Bean
	JdbcVerificationRepository jdbcVerificationRepository(JdbcTemplate jdbcTemplate) {
		return new JdbcVerificationRepository(jdbcTemplate);
	}
}
