package pl.smarttesting.loanorders.config;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Domyślna konfiguracja kontrolerów.
 */
@ControllerAdvice
@Configuration(proxyBeanMethods = false)
public class ControllerConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerConfig.class);

	@ExceptionHandler
	public void handle(Exception exception) throws Exception {
		LOGGER.error("Returning HTTP 400 Bad Request", exception);
		throw exception;
	}

	@Bean
	@Primary
	public Jackson2ObjectMapperBuilder objectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		return builder.modulesToInstall(new JavaTimeModule(), new Jdk8Module());
	}
}