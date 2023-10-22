package pl.smarttesting.verifier.model;

import java.util.UUID;

/**
 * Kontrakt na zweryfikowaną osobę.
 */
public interface VerifiedPerson {

	/**
	 * @return ID osoby
	 */
	UUID getUserId();

	/**
	 * @return PESEL osoby
	 */
	String getNationalIdentificationNumber();

	/**
	 * @return status osoby
	 */
	String getStatus();
}