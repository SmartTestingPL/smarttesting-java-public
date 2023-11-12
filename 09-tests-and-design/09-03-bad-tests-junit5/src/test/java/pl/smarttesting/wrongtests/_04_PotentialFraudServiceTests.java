package pl.smarttesting.wrongtests;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Klasa testowa pokazująca jak stanowość może pokrzyżować nam plany w powtarzalnych
 * wynikach testów.
 *
 * Najpierw zakomentuj {@link Disabled}, żeby wszystkie testy się uruchomiły.
 *
 * Następnie uruchom testy kilkukrotnie - zobaczysz, że czasami przechodzą, a czasami nie.
 * W czym problem?
 */
@Disabled
@TestMethodOrder(MethodOrderer.Random.class)
class _04_PotentialFraudServiceTests {

	/**
	 * Test ten oczekuje, że zawsze uruchomi się pierwszy. Dlatego oczekuje, że w cacheu
	 * będzie jeden wynik. Dla przypomnienia, cache jest współdzielony przez wszystkie
	 * testy, ponieważ jest statyczny.
	 *
	 * W momencie uruchomienia testów w innej kolejności, inne testy też dodają wpisy
	 * do cachea. Zatem nie ma możliwości, żeby rozmiar cachea wynosił 1.
	 */
	@Disabled
	@Test
	void should_count_potential_frauds() {
		PotentialFraudCache cache = new PotentialFraudCache();
		PotentialFraudService service = new PotentialFraudService(cache);

		service.setFraud("Kowalski");

		BDDAssertions.then(cache.fraudsSize()).isOne();
	}

	/**
	 * Przykład testu, który weryfikuje czy udało nam się dodać wpis do cachea.
	 * Zwiększa rozmiar cachea o 1. Gdy ten test zostanie uruchomiony przed
	 * {@link #should_count_potential_frauds} - wspomniany test się wywali.
	 */
	@Test
	void should_set_potential_fraud() {
		PotentialFraudCache cache = new PotentialFraudCache();
		PotentialFraudService service = new PotentialFraudService(cache);

		service.setFraud("Oszustowski");

		BDDAssertions.then(cache.fraud("Oszustowski")).isNotNull();
	}

	/**
	 * Potencjalne rozwiązanie problemu wspóldzielonego stanu. Najpierw
	 * zapisujemy stan wejściowy - jaki był rozmiar cachea. Dodajemy wpis
	 * do cachea i sprawdzamy czy udało się go dodać i czy rozmiar jest większy niż był.
	 *
	 * W przypadku uruchomienia wielu testów równolegle, sam fakt weryfikacji rozmiaru
	 * jest niedostateczny, gdyż inny test mógł zwiększyć rozmiar cachea. Koniecznym
	 * jest zweryfikowanie, że istnieje w cacheu wpis dot. Kradzieja.
	 *
	 * BONUS: Jeśli inny test weryfikował usunięcie wpisu z cachea, to asercja
	 * na rozmiar może nam się wysypać. Należy rozważyć, czy nie jest wystarczającym
	 * zweryfikowanie tylko obecności Kradzieja w cacheu!
	 */
	@Test
	void should_store_potential_fraud() {
		PotentialFraudCache cache = new PotentialFraudCache();
		PotentialFraudService service = new PotentialFraudService(cache);
		int initialSize = cache.fraudsSize();

		service.setFraud("Kradziej");

		BDDAssertions.then(cache.fraudsSize()).isGreaterThan(initialSize);
		BDDAssertions.then(cache.fraud("Kradziej")).isNotNull();
	}

}

class PotentialFraudCache {
	/**
	 * Stan współdzielony między instancjami. Problemy? Np. używamy
	 * niebezpiecznej dla wątków implementacji mapy.
	 */
	static final Map<String, PotentialFraud> cache = new HashMap<>();

	PotentialFraud fraud(String name) {
		return cache.get(name);
	}

	void put(PotentialFraud fraud) {
		cache.put(fraud.name, fraud);
	}

	int fraudsSize() {
		return cache.size();
	}
}

/**
 * Serwis aplikacyjny opakowujący wywołania do cachea.
 */
class PotentialFraudService {
	private final PotentialFraudCache cache;

	PotentialFraudService(PotentialFraudCache cache) {
		this.cache = cache;
	}

	void setFraud(String name) {
		this.cache.put(new PotentialFraud(name));
	}
}

/**
 * Struktura reprezentująca potencjalnego oszusta.
 */
class PotentialFraud {
	String name;

	PotentialFraud(String name) {
		this.name = name;
	}
}
