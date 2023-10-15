package pl.smarttesting.verifier.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smarttesting.verifier.model.VerificationRepository;
import pl.smarttesting.verifier.model.VerifiedPerson;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Komponent wykorzystujący {@link JdbcTemplate} do wykonania kwerend bazodanowych.
 * Dopiero w module, w którym pojawia nam się konkretny framework, implementujemy
 * interfejs {@link VerificationRepository} z domeny głównej.
 *
 * W ten sposób w domenie biznesowej nie mamy w ogóle elementów związanych z frameworkiem.
 */
class JdbcVerificationRepository implements VerificationRepository {

	private static final Logger log = LoggerFactory.getLogger(JdbcVerificationRepository.class);

	private final JdbcTemplate jdbcTemplate;

	JdbcVerificationRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public VerifiedPerson save(VerifiedPerson entity) {
		log.debug("Storing person [{}]", entity);
		jdbcTemplate.update("INSERT INTO verified " +
						"(user_id, " +
						"national_identification_number, " +
						"status) VALUES " +
						"(?, ?, ?)",
				entity.getUserId().toString(), entity.getNationalIdentificationNumber(), entity.getStatus());
		return entity;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<VerifiedPerson> findByUserId(UUID number) {
		List<VerifiedPersonEntity> verifiedPerson = jdbcTemplate
				.query("SELECT v.* FROM verified v WHERE v.user_id = ?", new BeanPropertyRowMapper<>(VerifiedPersonEntity.class), number.toString());
		log.debug("Found person [{}] by number [{}]", verifiedPerson.isEmpty(), number.toString());
		return verifiedPerson.isEmpty() ? Optional.empty() : Optional.of(verifiedPerson.get(0));
	}

	@Override
	@Transactional(readOnly = true)
	public long count() {
		Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM verified", Long.class);
		log.debug("Number of found verifications [{}]", count);
		return count != null ? count : 0;
	}
}

class VerifiedPersonEntity implements VerifiedPerson {

	private int id;

	private UUID userId;

	private String nationalIdentificationNumber;

	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	@Override
	public String getNationalIdentificationNumber() {
		return nationalIdentificationNumber;
	}

	public void setNationalIdentificationNumber(String nationalIdentificationNumber) {
		this.nationalIdentificationNumber = nationalIdentificationNumber;
	}

	@Override
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}