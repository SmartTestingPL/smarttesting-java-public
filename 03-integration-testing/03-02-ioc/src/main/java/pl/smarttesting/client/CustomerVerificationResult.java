package pl.smarttesting.client;

import java.util.UUID;

/**
 * Rezultat weryfikacji klienta.
 */
public class CustomerVerificationResult {
	private UUID userId;
	private Status status;

	private CustomerVerificationResult(UUID userId, Status status) {
		this.userId = userId;
		this.status = status;
	}

	public CustomerVerificationResult() {
	}

	public static CustomerVerificationResult passed(UUID userId) {
		return new CustomerVerificationResult(userId, Status.VERIFICATION_PASSED);
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

	enum Status {
		VERIFICATION_PASSED,
		VERIFICATION_FAILED
	}

}
