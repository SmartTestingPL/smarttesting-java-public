package pl.smarttesting.verifier;

/**
 * Komponent odpowiedzialny za wysyłanie wiadomości z oszustem.
 */
interface FraudAlertNotifier {
	/**
	 * Metoda wywołana w momencie, w którym znaleziono oszusta.
	 *
	 * @param customerVerification - weryfikacja klienta
	 */
	void fraudFound(CustomerVerification customerVerification);
}

