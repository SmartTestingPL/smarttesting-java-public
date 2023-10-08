package pl.smarttesting.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pl.smarttesting.e2e.customer.Customer;
import pl.smarttesting.e2e.customer.CustomerBuilder;
import pl.smarttesting.e2e.order.LoanOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Przykład wykorzystania dedykowanych narzędzi do testów po warstwie HTTP i generowania danych testowych.
 */
@EnabledIfSystemProperty(named = "e2e", matches = "true")
public class RestAssuredCustomerVerificationTest extends LoanOrdersTestBase {

	// Setup dla RestAssured
	@BeforeAll
	static void setUpAll() {
		RestAssured.baseURI = LOAN_ORDERS_URI_BASE;
		RestAssured.port = 9091;
	}

	// Test z użyciem RestAssured i Hamcrest Matchers; Klient generowany przy użyciu
	// biblioteki JFairy
	@Test
	void shouldSetOrderStatusToVerifiedWhenCorrectCustomer() {
		Customer correctCustomer = CustomerBuilder.create()
				.adultMale().build();
		given()
				.contentType(ContentType.JSON)
				.body(new LoanOrder(correctCustomer))
				.when()
				.post("/orders")
				.then()
				.statusCode(200)
				.body(instanceOf(String.class));
	}

}
