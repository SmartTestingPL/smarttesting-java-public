package pl.smarttesting.fraudverifier.config;

import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Klasa konfigurująca funkcjonalność Feature Toggles.
 */
@Configuration
public class FeatureTogglesConfig {

	// Wykorzystamy implementację interfejsu FeatureProvider bazującą na enumach.
	@Bean
	public FeatureProvider featureProvider() {
		return new EnumBasedFeatureProvider(Features.class);
	}
}
