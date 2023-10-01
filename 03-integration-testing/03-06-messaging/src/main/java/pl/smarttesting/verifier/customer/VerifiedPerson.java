package pl.smarttesting.verifier.customer;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

/**
 * Encja bazodanowa. Wykorzystujemy ORM (mapowanie obiektowo relacyjne) i obiekt
 * tej klasy mapuje się na wpis w mapowaniu klucz wartość o kluczu {@code verified}.
 */
@KeySpace("verified")
class VerifiedPerson {

	@Id
	private Long id;
	@NotNull
	private UUID userId;
	@NotBlank
	private String nationalIdentificationNumber;
	@NotBlank
	private String status;

	VerifiedPerson(UUID userId, String nationalIdentificationNumber, CustomerVerificationResult.Status status) {
		this.userId = userId;
		this.nationalIdentificationNumber = nationalIdentificationNumber;
		this.status = status.toString();
	}

	protected VerifiedPerson() { }

	public Long getId() {
		return id;
	}

	public void setID(Long id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public String getNationalIdentificationNumber() {
		return nationalIdentificationNumber;
	}

	public String getStatus() {
		return status;
	}
}
