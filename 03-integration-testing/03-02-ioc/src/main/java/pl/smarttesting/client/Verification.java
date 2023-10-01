package pl.smarttesting.client;

/**
 * Weryfikacja klienta.
 */
public interface Verification {

	/**
	 * Weryfikuje czy dana osoba nie jest oszustem.
	 * @return {@code false} dla oszusta.
	 */
	default boolean passes() {
		return false;
	};
}
