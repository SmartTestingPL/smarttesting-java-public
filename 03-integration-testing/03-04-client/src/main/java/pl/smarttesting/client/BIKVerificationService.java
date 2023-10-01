package pl.smarttesting.client;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Klient do komunikacji z Biurem Informacji Kredytowej. Posiada dwa konstruktory:
 * jeden, przyjmujący klienta HTTP, drugi tworzący go na podstawie wartości domyślnych.
 *
 * W tej implementacji chcemy pokazać jakie problemy można zaobserwować w momencie,
 * w którym nie weryfikujemy jakie wartości domyślne są używane przez nasze narzędzia.
 *
 */
public class BIKVerificationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(BIKVerificationService.class);

	private final String bikServiceUri;

	private final HttpClient client;

	public BIKVerificationService(String bikServiceUri, HttpClient client) {
		this.bikServiceUri = bikServiceUri;
		this.client = client;
	}

	// Konstruktor na potrzeby testu, pokazującego, że używanie wartości domyślnych
	// np. w Apache Http Client, może się źle skończyć
	BIKVerificationService(String bikServiceUri) {
		this.bikServiceUri = bikServiceUri;
		// Za pomocą wartości domyślnych tworzymy klienta http
		this.client = HttpClientBuilder.create().build();
	}

	/**
	 * Główna metoda biznesowa. Weryfikuje czy dana osoba jest oszustem poprzez
	 * wysłanie zapytania po HTTP do BIK. Do wykonania zapytania po HTTP wykorzystujemy
	 * bibliotekę Apache HTTP Client.
	 *
	 * @param customer - klient do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	public CustomerVerificationResult verify(Customer customer) {
		try {
			// W przypadku domyślnej konfiguracji, to zapytanie może trwać nieskończoność
			HttpResponse response = client
					.execute(new HttpGet(URI.create(bikServiceUri + customer.getPerson()
							.getNationalIdentificationNumber())));
			String externalStatus = EntityUtils.toString(response.getEntity());
			if (CustomerVerificationResult.Status.VERIFICATION_PASSED.name()
					.equals(externalStatus)) {
				return CustomerVerificationResult.passed(customer.getUuid());
			}
		}
		catch (IOException exception) {
			// wyłapujemy wyjątek związany z połączeniem i chcemy go przeprocesować
			processException(exception);
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}

	/**
	 * Domyślna implementacja loguje wyjątek do konsoli. Wyjątek nie jest ponownie rzucany.
	 * @param exception - wyjątek do obsłużenia
	 */
	void processException(IOException exception) {
		LOG.error("Http request execution failed.", exception);
	}

}
