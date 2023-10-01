package pl.smarttesting.verifier.customer;

/**
 * Komponent odpowiedzialny za nasłuchiwanie na wiadomości z oszustem.
 */
interface FraudListener {
	/**
	 * Metoda wywołana w momencie, w którym dostaliśmy notyfikację o oszuście.
	 *
	 * @param customerVerification - weryfikacja klienta
	 */
	void onFraud(CustomerVerification customerVerification);
}
