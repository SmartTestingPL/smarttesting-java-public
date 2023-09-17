package pl.smarttesting.bik.score.infrastructure;

import java.util.UUID;

import static pl.smarttesting.bik.score.infrastructure.CustomerVerificationResult.Status.VERIFICATION_PASSED;

/**
 * Rezultat weryfikacji klienta.
 *
 */
public class CustomerVerificationResult {

	private UUID userId;
	private Status status;

	protected CustomerVerificationResult(UUID userId, Status status) {
		this.userId = userId;
		this.status = status;
	}

	public CustomerVerificationResult() {
	}

	public static CustomerVerificationResult passed(UUID userId) {
		return new CustomerVerificationResult(userId, VERIFICATION_PASSED);
	}

	public static CustomerVerificationResult failed(UUID userId) {
		return new CustomerVerificationResult(userId, Status.VERIFICATION_FAILED);
	}

	public UUID getUserId() {
		return userId;
	}

	public Status getStatus() {
		return status;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean passed() {
		return VERIFICATION_PASSED.equals(status);
	}

	public enum Status {
		VERIFICATION_PASSED,
		VERIFICATION_FAILED
	}

	@Override
	public String toString() {
		return "CustomerVerificationResult{" +
				"userId=" + userId +
				", status=" + status +
				'}';
	}
}
