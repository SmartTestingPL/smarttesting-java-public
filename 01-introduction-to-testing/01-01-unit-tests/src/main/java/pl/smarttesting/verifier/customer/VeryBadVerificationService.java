package pl.smarttesting.verifier.customer;

/**
 * Przykład źle zaprojektowanego serwisu używającego metody statacznej do realizacji ciężkich operacji,
 * np. zapytań bazodanowych albo zapytań HTTP.
 * @see VeryBadVerificationServiceWrapper
 */
public class VeryBadVerificationService {

	public static boolean runHeavyQueriesToDatabaseFromStaticMethod() {
		// Metoda odpalająca ciężkie zapytania do bazy danych i ściągająca pół internetu.
		try {
			Thread.sleep(10000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
}
