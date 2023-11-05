package pl.smarttesting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Adnotacja {@link SpringBootApplication} sprawia, że wszystkie klasy z pakietu `pl.smarttesting`
 * oraz jego dzieci (np. `pl.smarttesting.verifier.infrastructure`) zostaną przez Springa przeskanowane
 * (np. w poszukiwaniu klas adnotowanych {@link Configuration}) i zarejestrowane w kontekście Springa.
 */
@SpringBootApplication
public class SmartTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartTestingApplication.class, args);
	}

}
