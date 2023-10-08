package pl.smarttesting.selenium.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Reprezentuje stronę dodawania właściciela.
 */
public class AddOwnerPage extends PageObject {

	// Przykład wykorzystania PageFactory
	@FindBy(id = "firstName")
	private WebElement firstNameInput;

	@FindBy(id = "lastName")
	private WebElement lastNameInput;

	@FindBy(id = "address")
	private WebElement addressInput;

	@FindBy(id = "city")
	private WebElement cityInput;

	@FindBy(id = "telephone")
	private WebElement telephoneNumberInput;

	@FindBy(xpath = "/html/body/div/div/form/div[2]/div/button")
	private WebElement addOwnerSubmit;

	// Obiekt `WebDriver` przekazywany w konstruktorach do obiektów kolejnych stron
	public AddOwnerPage(WebDriver driver) {
		super(driver);
	}

	// Operacje, które można wykonać na stronie

	public void fillFirstName(String firstName) {
		firstNameInput.sendKeys(firstName);
	}

	public void fillLastName(String lastName) {
		lastNameInput.sendKeys(lastName);
	}

	public void fillAddress(String address) {
		addressInput.sendKeys(address);
	}

	public void fillCity(String city) {
		cityInput.sendKeys(city);
	}

	public void fillTelephoneNumber(String telephoneNumber) {
		telephoneNumberInput.sendKeys(telephoneNumber);
	}

	// Zwraca obiekt kolejnej strony
	public OwnerViewPage addOwner() {
		addOwnerSubmit.click();
		return new OwnerViewPage(driver);
	}

}
