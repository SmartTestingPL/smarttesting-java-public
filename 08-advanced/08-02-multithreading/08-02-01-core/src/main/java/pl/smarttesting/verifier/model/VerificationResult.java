package pl.smarttesting.verifier.model;

import java.util.Objects;

/**
 * Rezultat weryfikacji klienta.
 */
public class VerificationResult {

	public final String verificationName;
	public final boolean result;

	public VerificationResult(String verificationName, boolean result) {
		this.verificationName = verificationName;
		this.result = result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VerificationResult that = (VerificationResult) o;
		return result == that.result &&
				Objects.equals(verificationName, that.verificationName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(verificationName, result);
	}

	@Override
	public String toString() {
		return "VerificationResult{" +
				"verificationName='" + verificationName + '\'' +
				", result=" + result +
				'}';
	}
}