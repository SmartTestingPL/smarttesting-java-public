package pl.smarttesting.verifier;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

/**
 * Interfejs rozszerzający {@link CrudRepository} z biblioteki Spring Data.
 * Dzięki jego rozszerzeniu mamy dostęp do wszystkich podstawowych operacji bazodanowych
 * dla obiektu typu {@link VerifiedPerson} o kluczy typu {@link Long}.
 *
 * Innymi słowy ten interfejs reprezentuje wszystkie główne akcje związane z CRUD
 * czyli Create, Read, Update oraz Delete. Dodatkowo jesteśmy w stanie zdefiniować
 * inne kwerendy, które zostaną przez bibliotekę Spring Data zaimplementowane w locie.
 */
interface VerificationRepository extends CrudRepository<VerifiedPerson, Long> {
	/**
	 * Kwerenda bazodanową wyszukująca użytkownika typu {@link VerifiedPerson} po numerze
	 * typu {@link UUID}.
	 * Spring Data sparsuje tekst {@code findByUserId} i znajdzie element {@code userId}.
	 * Ten mapowany jest na metodę {@link VerifiedPerson#getUserId()}.
	 * @param number- numer po którym poszukamy użytkownika
	 * @return potencjalny element w bazie danych
	 */
	Optional<VerifiedPerson> findByUserId(UUID number);
}