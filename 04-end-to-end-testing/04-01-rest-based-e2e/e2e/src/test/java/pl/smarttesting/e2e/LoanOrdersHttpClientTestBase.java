package pl.smarttesting.e2e;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import pl.smarttesting.e2e.customer.Customer;
import pl.smarttesting.e2e.order.LoanOrder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Przykład klasy basowej dla testów E2E po HTTP. Wszystkie szczegóły związane z implementacją
 * warstwy integracyjnej zostały wyniesione do metod pomocniczych przez co testy są o wiele bardziej czytelne.
 */
public class LoanOrdersHttpClientTestBase extends LoanOrdersTestBase {


	protected static CloseableHttpClient httpClient;
	protected ObjectMapper objectMapper;

	// Tworzenie klienta HTTP
	@BeforeAll
	static void setUpAll() {
		httpClient = HttpClientBuilder.create().build();
	}

	// Czyszczenie zasobów w tear-downie
	@AfterAll
	static void tearDownAll() throws IOException {
		httpClient.close();
	}

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModules(new JavaTimeModule(), new Jdk8Module());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	// Szczegóły techniczne wysyłania POSTa wyniesione do metody pomocniczej
	protected HttpPost buildPost(Customer customer) throws UnsupportedEncodingException, JsonProcessingException {
		HttpPost httpPost = new HttpPost(LOAN_ORDERS_URI);
		httpPost.setEntity(new StringEntity(objectMapper
				.writeValueAsString(new LoanOrder(customer))));
		httpPost.setHeader("Content-type", "application/json");
		return httpPost;
	}

	protected LoanOrder createAndRetrieveLoanOrder(HttpPost httpPost) throws IOException {
		HttpResponse postResponse = httpClient.execute(httpPost);
		assertThat(postResponse.getStatusLine().getStatusCode()).isEqualTo(200);
		String loanOrderId = EntityUtils.toString(postResponse.getEntity())
				.replaceAll("data:", "")
				.trim();

		HttpGet httpGet = new HttpGet(LOAN_ORDERS_URI + "/" + loanOrderId);

		// Zastosowanie Awaitility w celu uniknięcia false failures
		Awaitility.await()
				.pollInterval(10, TimeUnit.MILLISECONDS)
				.atMost(1, TimeUnit.MINUTES)
				.until(() -> {
					HttpResponse response = httpClient.execute(httpGet);
					return response.getStatusLine().getStatusCode() == 200;
				});
		httpGet.releaseConnection();
		HttpResponse getResponse = httpClient.execute(httpGet);
		return objectMapper.readValue(EntityUtils
						.toString(getResponse.getEntity()).replaceAll("data:", ""),
				LoanOrder.class);
	}
}
