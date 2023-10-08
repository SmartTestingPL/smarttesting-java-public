package pl.smarttesting.selenium.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Reprezentuje stronę wyszukiwania właścicieli.
 */
public class FindOwnersPage extends PageObject {

	// Przykład wykorzystania PageFactory
	@FindBy(linkText = "Add Owner")
	private WebElement addOwnerSubmit;

	// Obiekt `WebDriver` przekazywany w konstruktorach do obiektów kolejnych stron
	public FindOwnersPage(WebDriver driver) {
		super(driver);
	}

	// Zwraca obiekt kolejnej strony
	public AddOwnerPage navigateToAddOwner() {
		addOwnerSubmit.click();
		return new AddOwnerPage(driver);
	}

}
