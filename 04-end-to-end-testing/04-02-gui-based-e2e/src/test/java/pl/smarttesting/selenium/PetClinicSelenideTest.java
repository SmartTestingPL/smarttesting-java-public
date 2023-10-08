package pl.smarttesting.selenium;


import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

/**
 * Przykład testu z wykorzystaniem Selenide.
 */
@EnabledIfSystemProperty(named = "e2e", matches = "true")
public class PetClinicSelenideTest {

	@BeforeAll
	static void setUpAll() {
		baseUrl = "http://localhost:8080";
		browser = "chrome";
	}

	@AfterAll
	static void tearDownAll() {
		closeWebDriver();
	}

	@BeforeEach
	void setUp() {
		open("/");
	}

	@Test
	void shouldDisplayErrorMessage() {
		$(byLinkText("ERROR")).click();

		$(byText("Something happened...")).shouldBe(Condition.visible);
	}
}
