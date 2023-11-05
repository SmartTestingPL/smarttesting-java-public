package pl.smarttesting.verifier.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import pl.smarttesting.customer.Customer;
import pl.smarttesting.customer.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * Klasa testowa spinająca konfiguracje aplikacji w celu weryfikacji procesowania
 * na wielu wątkach.
 */
@SpringBootTest(classes = _02_CustomerVerifierTests.Config.class)
class _02_CustomerVerifierTests {

	/**
	 * Test uruchamia procesowanie klienta Stefan. Procesowanie zostanie ukończone w losowej
	 * kolejności, natomiast w naszym teście oczekujemy, że dostaniemy odpowiedź w kolejności
	 * weryfikacji age, id i na końcu name.
	 *
	 * Na wszelki wypadek uruchamiamy test 5 razy, żeby upewnić się, że za każdym razem przejdzie.
	 *
	 * Zakomentuj adnotację {@link Disabled}, żeby przekonać się, że test może nie przejść!
	 *
	 * @param verifier - komponent do przetestowania
	 */
	@Disabled
	@DisplayName("Verification Result")
	@RepeatedTest(value = 5, name = "{displayName} - repetition {currentRepetition} of {totalRepetitions}")
	void should_return_results_in_order_of_execution(@Autowired _01_CustomerVerifier verifier) {
		List<VerificationResult> results = verifier.verify(new Customer(UUID.randomUUID(), tooYoungStefan()));

		then(results)
				.extracting("verificationName")
				.containsExactly("age", "id", "name");
	}

	/**
	 * Test uruchamia procesowanie klienta Stefan. Procesowanie zostanie ukończone w losowej
	 * kolejności. W naszym teście oczekujemy, że dostaniemy odpowiedź zawierającą wszystkie
	 * 3 weryfikacje w losowej kolejności.
	 *
	 * @param verifier - komponent do przetestowania
	 */
	@DisplayName("Verification Result")
	@RepeatedTest(value = 5, name = "{displayName} - repetition {currentRepetition} of {totalRepetitions}")
	void should_work_in_parallel_with_less_constraint(@Autowired _01_CustomerVerifier verifier) {
		List<VerificationResult> results = verifier.verify(new Customer(UUID.randomUUID(), tooYoungStefan()));

		then(results)
				.extracting("verificationName")
				.containsOnly("age", "id", "name");
	}

	/**
	 * Testujemy asynchroniczne procesowanie zdarzeń. Po każdej weryfikacji zostaje wysłane zdarzenie,
	 * które komponent {@link _03_VerificationListener} trzyma w kolejce.
	 *
	 * Procesowanie jest asynchroniczne, a test na to nie reaguje. Po zakolejkowaniu wywołania
	 * wywołań asynchronicznych od razu przechodzi do asercji zapisanych zdarzeń w kolejce. Problem w tym,
	 * że procesowanie jeszcze trwa! Innymi słowy test jest szybszy niż kod, który testuje.
	 *
	 * Zakomentuj adnotację {@link Disabled}, żeby przekonać się, że test może nie przejść!
	 *
	 * @param verifier - komponent do przetestowania - wysyłający zdarzenia
	 * @param verificationListener - komponent reagujący na zdarzenia
	 */
	@Disabled
	@DisplayName("Verification Listener")
	@RepeatedTest(value = 5, name = "{displayName} - repetition {currentRepetition} of {totalRepetitions}")
	void should_work_in_parallel_without_a_sleep(@Autowired _01_CustomerVerifier verifier, @Autowired _03_VerificationListener verificationListener)  {
		verifier.verifyAsync(new Customer(UUID.randomUUID(), tooYoungStefan()));

		then(verificationListener.events)
				.extracting("sourceDescription")
				.containsOnly("age", "id", "name");
	}

	/**
	 * Próba naprawy sytuacji z testu powyżej.
	 *
	 * Zakładamy, że w ciągu 4 sekund zadania powinny się ukończyć, a zdarzenia powinny zostać wysłane.
	 *
	 * Rozwiązanie to w żaden sposób się nie skaluje i jest marnotrawstwem czasu. W momencie, w którym
	 * procesowanie ukończy się po np. 100 ms, zmarnujemy 3.9 sekundy by dokonać asercji.
	 *
	 * @param verifier - komponent do przetestowania - wysyłający zdarzenia
	 * @param verificationListener - komponent reagujący na zdarzenia
	 * @throws InterruptedException
	 */
	@DisplayName("Verification Listener")
	@RepeatedTest(value = 5, name = "{displayName} - repetition {currentRepetition} of {totalRepetitions}")
	void should_work_in_parallel_with_a_sleep(@Autowired _01_CustomerVerifier verifier, @Autowired _03_VerificationListener verificationListener) throws InterruptedException {
		verifier.verifyAsync(new Customer(UUID.randomUUID(), tooYoungStefan()));

		Thread.sleep(4000);

		then(verificationListener.events)
				.extracting("sourceDescription")
				.containsOnly("age", "id", "name");
	}

	/**
	 * Najlepsze rozwiązanie problemu.
	 *
	 * Wykorzystujemy bibliotekę Awaitility, która stosuje polling - czyli poczeka maksymalnie 5 sekund
	 * i będzie co 100 milisekund weryfikować rezultat asercji. W ten sposób maksymalnie będziemy spóźnieni
	 * wobec uzyskanych wyników o ok. 100 ms.
	 *
	 * @param verifier - komponent do przetestowania - wysyłający zdarzenia
	 * @param verificationListener - komponent reagujący na zdarzenia
	 */
	@DisplayName("Verification Listener")
	@RepeatedTest(value = 5, name = "{displayName} - repetition {currentRepetition} of {totalRepetitions}")
	void should_work_in_parallel_with_awaitility(@Autowired _01_CustomerVerifier verifier, @Autowired _03_VerificationListener verificationListener) {
		verifier.verifyAsync(new Customer(UUID.randomUUID(), tooYoungStefan()));

		Awaitility.await()
				.atMost(5, TimeUnit.SECONDS)
				.pollInterval(100, TimeUnit.MILLISECONDS)
				.untilAsserted(() -> {
			then(verificationListener.events)
					.extracting("sourceDescription")
					.containsOnly("age", "id", "name");
		});
	}

	Person tooYoungStefan() {
		return new Person("", "", LocalDate.now(), Person.GENDER.MALE, "0123456789");
	}

	@Autowired
	_03_VerificationListener verificationListener;

	@AfterEach
	void cleanup() {
		verificationListener.events.clear();
	}


	/**
	 * Konfiguracja testowa.
	 * - włączamy procesowanie asynchroniczne ({@link EnableAsync}
	 * - włączamy autokonfigurację ({@link EnableAutoConfiguration})
	 * - włączamy skanowanie pakietów w celu znalezienia zarejestrowanych pakietowych komponentów
	 * weryfikacji
	 */
	@Configuration(proxyBeanMethods = false)
	@EnableAsync(proxyTargetClass = true)
	@EnableAutoConfiguration
	@ComponentScan(basePackages = "pl.smarttesting.verifier.model.verification")
	static class Config extends CustomerConfiguration {

	}
}
