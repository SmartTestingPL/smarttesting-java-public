package pl.smarttesting.verifier.model;

import java.util.Optional;
import java.util.UUID;

/**
 * Kontrakt na zapis i odczyt elementów związanych z weryfikacją.
 */
public interface VerificationRepository {

	/**
	 * Zapisuje dany obiekt.
	 *
	 * @param entity - obiekt do zapisu
	 * @return zapisany obiekt
	 */
	VerifiedPerson save(VerifiedPerson entity);

	/**
	 * Wyszukuje zweryfikowanej osoby po ID.
	 *
	 * @param number - id po którym będziemy wyszukiwać osoby
	 * @return zweryfikowana osoba opakowana w {@link Optional}
	 */
	Optional<VerifiedPerson> findByUserId(UUID number);

	/**
	 * @return suma zapisanych elementów
	 */
	long count();
}