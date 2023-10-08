package pl.smarttesting.selenium;

import java.time.Duration;

import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Przykład źle napisanego testu z wykorzystaniem WebDrivera.
 */
@EnabledIfSystemProperty(named = "e2e", matches = "true")
public class PetClinicTest {

	private WebDriver driver;
	private Fairy fairy;

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
	}

	// Test co prawda coś weryfikuje, ale jest bardzo nieczytelny i trudny do utrzymania.
	@Test
	void shouldAddOwner() {
		Person person = fairy.person();

		WebElement findOwners = new WebDriverWait(driver, Duration.ofSeconds(20))
				.until(driver -> driver.findElement(By.linkText("FIND OWNERS")));
		findOwners.click();
		// Zastosowanie waitów przy przejściach na kolejne strony
		WebElement addOwnerButton = new WebDriverWait(driver, Duration.ofSeconds(20))
				.until(driver -> driver.findElement(By.linkText("Add Owner")));
		addOwnerButton.click();
		WebElement firstNameInput = new WebDriverWait(driver, Duration.ofSeconds(20))
				.until(driver -> driver.findElement(By.id("firstName")));
		firstNameInput.sendKeys(person.getFirstName());
		WebElement lastNameInput = driver.findElement(By.id("lastName"));
		lastNameInput.sendKeys(person.getLastName());
		WebElement addressInput = driver.findElement(By.id("address"));
		addressInput.sendKeys(person.getAddress().toString());
		WebElement cityInput = driver.findElement(By.id("city"));
		cityInput.sendKeys(person.getAddress().getCity());
		WebElement telephoneInput = driver.findElement(By.id("telephone"));
		telephoneInput.sendKeys(person.getTelephoneNumber().replaceAll("-", ""));
		WebElement addOwnerSubmit = driver
				.findElement(By.xpath("/html/body/div/div/form/div[2]/div/button"));
		addOwnerSubmit.click();
		new WebDriverWait(driver, Duration.ofSeconds(20))
				.until(driver -> driver.getPageSource().contains("Owner Information"));

		assertThat(driver.getPageSource()).contains(person.getFullName());
	}

	// Zamknięcie Driver'a (i okna przeglądarki) po teście
	@AfterEach
	public void tearDown() {
		driver.quit();
	}

}
