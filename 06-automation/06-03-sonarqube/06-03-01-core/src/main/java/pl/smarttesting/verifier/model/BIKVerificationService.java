package pl.smarttesting.verifier.model;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.customer.Customer;

/**
 * Klient do komunikacji z Biurem Informacji Kredytowej.
 */
class BIKVerificationService {

	private static final Logger LOG = LoggerFactory
			.getLogger(BIKVerificationService.class);

	private final String bikServiceUri;

	private final HttpClient client;

	public BIKVerificationService(String bikServiceUri) {
		this.bikServiceUri = bikServiceUri;
		this.client = HttpClientBuilder.create().build();
	}

	/**
	 * Weryfikuje czy dana osoba jest oszustem.
	 *
	 * @param customer - klient do zweryfikowania
	 * @return rezultat weryfikacji
	 */
	public CustomerVerificationResult verify(Customer customer) {
		try {
			Thread.sleep(300);
			return pass(customer);
		}
		catch (Exception exception) {
			LOG.error("Http request execution failed.", exception);
		}
		return CustomerVerificationResult.failed(customer.getUuid());
	}

	CustomerVerificationResult pass(Customer customer) {
		return CustomerVerificationResult.passed(customer.getUuid());
	}

	/**
	 * Bardzo skomplikowana metoda wykorzystana na slajdach w celu ukazania wysokiego skomplikowania cyklomatycznego.
	 */
	public int complexMethod(int a, int b, int c) {
		int d = a + 2;
		int e = a > 0 ? d + 5 : c;
		int f = d > 0 ? e + 5 : a;
		int result = 0;
		if (a > b || f > 1 && d + 1 > 3 || f < 4) {
			return 8;
		}
		if (a > c && e > f || a > 1 && e + 1 > 3 || d < 4) {
			return  1;
		} else {
			if (a + 1 > c - 1 || a > b + 3 || f > 19) {
				return 1233;
			}
			if (e < a && d > c) {
				if (a + 4 > b - 2) {
					if (c - 5 < a + 11) {
						return 81;
					} else if (a > c) {
						return 102;
					}
				}
				if (a > c + 21 && e > f - 12) {
					return 13;
				} else {
					if (a + 10 > c - 1) {
						return 123;
					} else if (e + 1 < a && d + 14 > c) {
						return 111;
					}
					if (f > 10) {
						return 1;
					}
				}
			}
		}
		return result;
	}

}
