package pl.smarttesting.verifier;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(passed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		VerificationEvent other = (VerificationEvent) obj;
		return passed == other.passed;
	}

}
