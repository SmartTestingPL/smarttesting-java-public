package pl.smarttesting.e2e;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import pl.smarttesting.e2e.customer.Customer;
import pl.smarttesting.e2e.customer.CustomerBuilder;
import pl.smarttesting.e2e.customer.Person;
import pl.smarttesting.e2e.order.LoanOrder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static pl.smarttesting.e2e.order.LoanOrder.Status.VERIFIED;

/**
 * Przykłady testów E2E po HTTP: gorszy i lepszy.
 */
@EnabledIfSystemProperty(named = "e2e", matches = "true")
public class HttpClientBasedCustomerVerificationTest extends LoanOrdersHttpClientTestBase {


	// Test mało czytelny ze względu na zbyt dużo kodu boiler-plate i mieszanie poziomów
	// abstrakcji, brak sensownej obsługi timeout'ów. Dodatkowo, z powodu źle zsetupowanego (domyślnego)
	// ApacheHttpClienta, test nie ma w ogóle timeoutu w warstwie klienta HTTP i przy niektórych błędach
	// będzie wisiał w nieskończoność.
	@Test
	void shouldSetOrderStatusToVerifiedWhenCorrectCustomer() throws IOException {
		// given
		Customer correctCustomer = CustomerBuilder.create().build();
		HttpPost httpPost = new HttpPost(LOAN_ORDERS_URI);
		httpPost.setEntity(new StringEntity(objectMapper
				.writeValueAsString(new LoanOrder(correctCustomer))));
		httpPost.setHeader("Content-type", "application/json");

		// when
		HttpResponse postResponse = httpClient.execute(httpPost);

		// then
		assertThat(postResponse.getStatusLine().getStatusCode()).isEqualTo(200);

		// when
		String loanOrderId = EntityUtils.toString(postResponse.getEntity())
				.replaceAll("data:", "")
				.trim();
		HttpGet httpGet = new HttpGet(LOAN_ORDERS_URI + "/" + loanOrderId);
		httpGet.setHeader("Content-type", "application/json");

		HttpResponse getResponse = httpClient.execute(httpGet);

		// then
		LoanOrder loanOrder = objectMapper.readValue(EntityUtils
						.toString(getResponse.getEntity()).replaceAll("data:", ""),
				LoanOrder.class);
		assertThat(loanOrder.getStatus()).isEqualTo(VERIFIED);
	}


	// Boiler-plate i setup wyniesiony do metod pomocniczych w klasie bazowej; zastosowanie
	// wzorca AssertObject
	@Test
	void shouldSetOrderStatusToFailedWhenInCorrectCustomer() throws IOException {
		// given
		Customer correctCustomer = CustomerBuilder.create()
				.withGender(Person.GENDER.MALE)
				.build();

		// when
		LoanOrder loanOrder = createAndRetrieveLoanOrder(buildPost(correctCustomer));

		// then
		new LoanOrderAssert(loanOrder).customerVerificationFailed();
	}

}
