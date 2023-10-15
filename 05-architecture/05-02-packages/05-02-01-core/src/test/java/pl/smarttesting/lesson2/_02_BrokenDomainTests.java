package pl.smarttesting.lesson2;

import org.junit.jupiter.api.Test;

/**
 * Kod do slajdu [Jaki problem chcemy rozwiązać?]
 */
public class _02_BrokenDomainTests {

	/**
	 * Tu mamy przykład jak w kodzie backendowym możemy tworzyć elementy UI.
	 * Niestety, skoro w tym samym kodzie mamy dostęp do innych komponentów,
	 * np. do repozytoriów nad bazami danych. Nic nie szkodzi na przeszkodzie,
	 * żeby z kodu kliknięcia w przycisk uruchomić jakieś zapytanie SQL...
	 */
	@Test
	void should_present_broken_domain() {
		UI ui = new UI(new Button(new Repository()));
		if (ui.userClicked()) {
			// z UI zapisz w bazie danych
			Loan loan = ui.button.repository.save(new Loan());
			ui.button.showInUi(loan.client.marketing.homeAddress);
		}
	}
}


class UI {
	final Button button;

	UI(Button button) {
		this.button = button;
	}

	boolean userClicked() {
		return true;
	}

	Loan pickedLoan() {
		return null;
	}
}

class Button {
	final Repository repository;

	Button(Repository repository) {
		this.repository = repository;
	}

	void showInUi(HomeAddress homeAddress) {

	}

}

class Repository {
	<T> T save(T loan) {
		return loan;
	};
}

class Client {
	Marketing marketing = new Marketing();
}

class Marketing {
	HomeAddress homeAddress = new HomeAddress();
}

class Discounts {

}

class HomeAddress {

}

class Loan {
	Client client = new Client();
	Marketing marketing = new Marketing();
	Discounts discounts = new Discounts();
}