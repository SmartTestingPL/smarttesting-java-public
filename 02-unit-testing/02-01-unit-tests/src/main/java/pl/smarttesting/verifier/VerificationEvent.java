package pl.smarttesting.verifier;

/**
 * Zdarzenie związane z weryfikacją klienta.
 */
public class VerificationEvent {

	private final boolean passed;

	public VerificationEvent(boolean passed) {
		this.passed = passed;
	}

	public boolean passed() {
		return passed;
	}
}
