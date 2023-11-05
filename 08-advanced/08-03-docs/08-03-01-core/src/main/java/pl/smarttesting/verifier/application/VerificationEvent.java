package pl.smarttesting.verifier.application;

import org.springframework.context.ApplicationEvent;

/**
 * Zdarzenie związane z weryfikacją klienta.
 */
public class VerificationEvent extends ApplicationEvent {

	private final String sourceDescription;

	private final boolean verificationSuccessful;

	/**
	 * Tworzy nowe zdarzenie.
	 *
	 * @param source źródło (obiekt), z którego zdarzenie zostało rzucone. Nigdy {@code null}
	 * @param sourceDescription - opis źródła
	 * @param verificationSuccessful - czy weryfikacja była udana czy nie
	 */
	public VerificationEvent(Object source, String sourceDescription, boolean verificationSuccessful) {
		super(source);
		this.sourceDescription = sourceDescription;
		this.verificationSuccessful = verificationSuccessful;
	}

	@Override
	public String toString() {
		return "VerificationEvent{" +
				"verificationSuccessful=" + verificationSuccessful +
				", source=" + source +
				'}';
	}
}
