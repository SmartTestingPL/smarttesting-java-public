package pl.smarttesting.verifier.customer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import jakarta.persistence.metamodel.SingularAttribute;

import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.data.keyvalue.repository.support.SimpleKeyValueRepository;
import org.springframework.data.map.MapKeyValueAdapter;

/**
 * Abstrakcja nad zwykłą mapę symulującą bazę danych. Kod wygląda na bardziej skomplikowany
 * niż powinien, gdyż chcemy go wpiąć w istniejące rozwiązanie Spring Data.
 * W innych językach programowania mogłaby to być po prostu zwykła mapa.
 */
public class _02_InMemoryVerificationRepository extends SimpleKeyValueRepository<VerifiedPerson, Long> implements VerificationRepository {

	public _02_InMemoryVerificationRepository() {
		super(new JpaEntityInformationSupport<VerifiedPerson, Long>(VerifiedPerson.class) {
			@Override
			public Long getId(VerifiedPerson entity) {
				return entity.getId();
			}

			@Override
			public Class<Long> getIdType() {
				return Long.class;
			}

			@Override
			public SingularAttribute<? super VerifiedPerson, ?> getIdAttribute() {
				return null;
			}

			@Override
			public boolean hasCompositeId() {
				return false;
			}

			@Override
			public Collection<String> getIdAttributeNames() {
				return Collections.singleton("id");
			}

			@Override
			public Object getCompositeIdAttributeValue(Object id, String idAttribute) {
				return null;
			}

			@Override
			public Map<String, Object> getKeyset(Iterable<String> propertyPaths, VerifiedPerson entity) {
				return null;
			}
		}, new KeyValueTemplate(new MapKeyValueAdapter()));
	}

	@Override
	public Optional<VerifiedPerson> findByUserId(UUID number) {
		return findAll().stream().filter(p -> number.equals(p.getUserId())).findAny();
	}

	@Override
	public <S extends VerifiedPerson> S save(S entity) {
		if (entity.getId() == null) {
			entity.setID(new Random().nextLong());
		}
		return super.save(entity);
	}
}
