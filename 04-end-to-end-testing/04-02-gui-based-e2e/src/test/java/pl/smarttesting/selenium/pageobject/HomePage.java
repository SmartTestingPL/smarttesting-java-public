package pl.smarttesting.selenium.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Reprezentuje stronę startową.
 */
public class HomePage extends PageObject {

	// Przykład wykorzystania PageFactory
	@FindBy(linkText = "FIND OWNERS")
	private WebElement findOwnersLink;

	// Obiekt `WebDriver` przekazywany w konstruktorach do obiektów kolejnych stron
	public HomePage(WebDriver driver) {
		super(driver);
	}

	// Zwraca obiekt kolejnej strony
	public FindOwnersPage navigateToFindOwners() {
		findOwnersLink.click();
		return new FindOwnersPage(driver);
	}

}
