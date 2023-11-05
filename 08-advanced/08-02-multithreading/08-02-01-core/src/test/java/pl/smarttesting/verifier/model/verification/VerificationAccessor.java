package pl.smarttesting.verifier.model.verification;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mockito.BDDMockito;
import pl.smarttesting.verifier.model.Verification;

import org.springframework.context.ApplicationEventPublisher;

/**
 * Z powodu ogarniczenia dostępności do pakietowej, tworzymy klasę pomocniczą
 * z dostępem publicznym, która ma dostęp do pakietowych klas.
 */
public class VerificationAccessor {

	/**
	 * Zwraca weryfikacje z zamockowaną publikacją eventów.
	 *
	 * @return set z produkcyjnym weryfikacjami
	 */
	public static Set<Verification> verifications() {
		ApplicationEventPublisher mock = BDDMockito.mock(ApplicationEventPublisher.class);
		return new HashSet<>(Arrays.asList(new AgeVerification(mock), new IdentificationNumberVerification(mock), new NameVerification(mock)));
	}
}