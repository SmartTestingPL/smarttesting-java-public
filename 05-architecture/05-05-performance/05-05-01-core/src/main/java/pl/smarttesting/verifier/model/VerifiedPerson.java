package pl.smarttesting.verifier.model;

/**
 * Kontrakt na zweryfikowaną osobę.
 */
public interface VerifiedPerson {

	/**
	 * @return ID osoby
	 */
	String getUserId();

	/**
	 * @return PESEL osoby
	 */
	String getNationalIdentificationNumber();

	/**
	 * @return status osoby
	 */
	String getStatus();
}