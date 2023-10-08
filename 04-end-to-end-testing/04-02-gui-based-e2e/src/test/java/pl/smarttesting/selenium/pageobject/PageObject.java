package pl.smarttesting.selenium.pageobject;

import java.time.Duration;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Klasa bazowa dla wszystkich klas reprezentujących poszczególne strony.
 * Zawiera setup webDrivera i weryfikacji czy strona została już załadowana.
 */
abstract class PageObject {

	protected final WebDriver driver;

	protected PageObject(WebDriver driver) {
		this.driver = driver;
		// Metoda z użyciem waita weryfikująca czy strona została załadowana
		pageReady();
		// Inicjalizacja elementów zaanotowanych @FindBy
		PageFactory.initElements(driver, this);
	}

	public boolean containsText(String text) {
		return driver.getPageSource().contains(text);
	}

	// Metoda z użyciem waita weryfikująca czy strona została załadowana
	private void pageReady() {
		ExpectedCondition<Boolean> pageReadyCondition = driver -> ((JavascriptExecutor) driver)
				.executeScript("return document.readyState;")
				.equals("complete");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		wait.until(pageReadyCondition);
	}
}
