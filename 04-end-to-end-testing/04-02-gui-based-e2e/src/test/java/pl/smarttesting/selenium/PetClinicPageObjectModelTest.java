package pl.smarttesting.selenium;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pl.smarttesting.selenium.pageobject.AddOwnerPage;
import pl.smarttesting.selenium.pageobject.FindOwnersPage;
import pl.smarttesting.selenium.pageobject.HomePage;
import pl.smarttesting.selenium.pageobject.OwnerViewPage;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Przykład zastosowania wzorca PageObjectModel.
 */
@EnabledIfSystemProperty(named = "e2e", matches = "true")
public class PetClinicPageObjectModelTest {

	private WebDriver driver;
	private Fairy fairy;
	private HomePage homePage;

	// Wersję drivera odpowiednią dla wersji przeglądarki i systemu operacyjnego
	// można ściągnąć z https://googlechromelabs.github.io/chrome-for-testing/
	// Potem lokalizację drivera trzeba ustawić jako System Property
	@BeforeAll
	static void setUpAll() {
		System.setProperty("webdriver.chrome.driver", "./src/test/resources/drivers/chromedriver");
	}

	@BeforeEach
	void setUp() {
		fairy = Fairy.create();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		driver = new ChromeDriver(options);
		driver.get("http://localhost:8080");
		homePage = new HomePage(driver);
	}

	// Test z wykorzystaniem Selenium PageObjectModel
	@Test
	void shouldAddOwner() {
		Person person = fairy.person();
		FindOwnersPage findOwnersPage = homePage.navigateToFindOwners();
		AddOwnerPage addOwnerPage = findOwnersPage.navigateToAddOwner();
		fillOwnerData(addOwnerPage, person);

		OwnerViewPage ownerViewPage = addOwnerPage.addOwner();

		assertThat(ownerViewPage.containsText(person.getFullName())).isTrue();
	}

	// Boiler-plate wyniesiony do metody pomocniczej
	private void fillOwnerData(AddOwnerPage addOwnerPage, Person person) {
		addOwnerPage.fillFirstName(person.getFirstName());
		addOwnerPage.fillLastName(person.getLastName());
		addOwnerPage.fillAddress(person.getAddress().toString());
		addOwnerPage.fillCity(person.getAddress().getCity());
		addOwnerPage.fillTelephoneNumber(person.getTelephoneNumber().replaceAll("-", ""));
	}

	// Zamknięcie Driver'a (i okna przeglądarki) po teście
	@AfterEach
	public void tearDown() {
		driver.quit();
	}

}
