package pl.smarttesting.verifier;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Encja bazodanowa. Wykorzystujemy ORM (mapowanie obiektowo relacyjne) i obiekt
 * tej klasy mapuje się na tabelę {@code verified}. Każde pole klasy to osobna kolumna
 * w bazie danych.
 */
@Entity
@Table(name = "verified")
class VerifiedPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	Long getId() {
		return id;
	}

	void setID(Long id) {
		this.id = id;
	}

	UUID getUserId() {
		return userId;
	}

	String getNationalIdentificationNumber() {
		return nationalIdentificationNumber;
	}

	String getStatus() {
		return status;
	}
}
