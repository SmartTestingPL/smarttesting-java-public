package pl.smarttesting.lesson1;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Klasa z przykładami źle zaprojektowanego kodu.
 *
 * Testy nie zawierają asercji, są tylko po to by skopiować je na slajdy
 * z pewnością, że się nadal kompilują.
 *
 * Klasy implementacyjne poniżej nic nie robią. Chodzi jedynie o zaprezentowanie
 * pewnych koncepcji.
 */
public class _01_BadClassTests {

	/**
	 * Kod widoczny na slajdzie [Testy a architektura].
	 * W komentarzach zakładamy pewne kwestie po spojrzeniu w
	 * wyimaginowaną implementację (oczywiście na
	 * potrzeby tej lekcji takiej implementacji nie ma - chodzi nam o pewne koncepcje).
	 */
	@Test
	void should_build_an_object() {
		// imię musi być null, żeby uruchomić specjalny scenariusz uruchomienia
		// nie chcemy liczyć dodatkowych opłat ze względu na konto, zatem ustawiamy je na null
		// w kodzie okazuje się, że nikt nie korzysta z usług marketingu więc też ustawiamy na null
		Person person = new Person(null, "Kowalski", null, new PhoneService(), null, new TaxService(), null);
	}

	/**
	 * Czy zdarzyło Ci się, że dodawanie kolejnych testów było dla Ciebie drogą przez mękę?
	 * Czy znasz przypadki, gdzie potrzebne były setki linijek kodu przygotowującego pod uruchomienie testu?
	 * Oznacza to, że najprawdopodobniej albo nasz sposób testowania jest niepoprawny
	 * albo architektura aplikacji jest zła.
	 */
	@Test
	void should_use_a_lot_of_mocks() {
		AccountService accountService = mock(AccountService.class);
		when(accountService.calculate()).thenReturn("PL123080123");
		PhoneService phoneService = mock(PhoneService.class);
		when(phoneService.calculate()).thenReturn("+4812371237");
		MarketingService marketingService = mock(MarketingService.class);
		when(marketingService.calculate()).thenReturn("MAIL");
		TaxService taxService = mock(TaxService.class);
		when(taxService.calculate()).thenReturn("1_000_000PLN");
		ReportingService reportingService = mock(ReportingService.class);
		when(taxService.calculate()).thenReturn("FAILED");
		Person person = new Person("Jan", "Kowalski", accountService, phoneService, reportingService);
		person.marketingService = marketingService;
		person.taxService = taxService;
		// nie rozumiem różnicy między calculate i count
		person.calculate();
		person.count();
		person.order();
	}
}

class Person {
	String name;
	String surName;
	AccountService account;
	PhoneService phoneService;
	MarketingService marketingService;
	TaxService taxService;
	ReportingService reportingService;

	Person(String name, String surName, AccountService account,
			PhoneService phoneService,
			MarketingService marketingService,
			TaxService taxService,
			ReportingService reportingService) {
		this.name = name;
		this.surName = surName;
		this.account = account;
		this.phoneService = phoneService;
		this.marketingService = marketingService;
		this.taxService = taxService;
		this.reportingService = reportingService;
	}

	Person(String name, String surName, AccountService account,
			PhoneService phoneService,
			ReportingService reportingService) {
		this.name = name;
		this.surName = surName;
		this.account = account;
		this.phoneService = phoneService;
		this.reportingService = reportingService;
	}

	String order() {
		return "ordered";
	}

	String count() {
		return "calculated";
	}

	String calculate() {
		return "calculated";
	}
}

class AccountService {
	String calculate() {
		return "";
	}
}

class PhoneService {
	String calculate() {
		return "";
	}
}

class TaxService {
	String calculate() {
		return "";
	}
}

class MarketingService {
	String calculate() {
		return "";
	}
}

class ReportingService {
	String calculate() {
		return "";
	}
}
