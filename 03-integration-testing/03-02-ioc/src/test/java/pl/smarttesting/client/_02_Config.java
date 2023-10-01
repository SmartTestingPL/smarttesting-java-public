package pl.smarttesting.client;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kod przedstawiony na slajdzie po zdefiniowaniu Inversion of Control.
 * Pokazujemy na przykładzie frameworku Spring, jak można odseparować
 * konfigurację konstrukcji obiektów od ich faktycznego użycia.
 */

// Konfiguracja opisująca za pomocą metod konstrukcję obiektów
@Configuration(proxyBeanMethods = false)
class _02_Config {

	// Opis utworzenia obiektu
	@Bean
	HttpCallMaker httpCallMaker() { return new HttpCallMaker(); }

	@Bean
	DatabaseAccessor accessor() { return new DatabaseAccessor(); }

	// Argumentami metody są zależności,
	// które chcemy żeby zostały wstrzyknięte
	@Bean
	AgeVerification ageVerification(HttpCallMaker httpCallMaker, DatabaseAccessor accessor) {
		return new AgeVerification(httpCallMaker, accessor);
	}

	@Bean
	IdentificationNumberVerification idVerification(DatabaseAccessor accessor) {
		return new IdentificationNumberVerification(accessor);
	}

	@Bean
	EventEmitter eventEmitter()  { return new EventEmitter(); }

	@Bean
	NameVerification nameVerification(EventEmitter eventEmitter) {
		return new NameVerification(eventEmitter);
	}

	// Kontener jest w stanie znaleźć wszystkie obiekty danego typu
	// i umieścić je w kolekcji
	@Bean
	CustomerVerifier customerVerifier(Set<Verification> verifications) {
		return new CustomerVerifier(verifications);
	}

}
