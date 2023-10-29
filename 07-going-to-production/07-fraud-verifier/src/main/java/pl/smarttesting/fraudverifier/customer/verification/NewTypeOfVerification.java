package pl.smarttesting.fraudverifier.customer.verification;

import pl.smarttesting.fraudverifier.Verification;
import pl.smarttesting.fraudverifier.customer.Person;

/**
 * Jakiś nowy typ weryfikacji, który chcemy dodać przy kolejnym wydaniu. Zostanie on wprowadzony
 * do kodu na produkcję przy wdrożeniu, ale uruchamiać go będziemy dopiero za pomocą feature toggle.
 */
public class NewTypeOfVerification implements Verification {


	@Override
	public boolean passes(Person person) {
		return false;
	}
}
