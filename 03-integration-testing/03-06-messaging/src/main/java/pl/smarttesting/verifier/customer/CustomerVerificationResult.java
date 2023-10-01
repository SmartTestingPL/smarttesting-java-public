package pl.smarttesting.verifier.customer;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static pl.smarttesting.verifier.customer.CustomerVerificationResult.Status.VERIFICATION_PASSED;

/**
 * Rezultat weryfikacji klienta.
 */
public class CustomerVerificationResult implements Serializable {

	private UUID userId;
	private Status status;

	@JsonCreator
	protected CustomerVerificationResult(@JsonProperty("userId") UUID userId,
			@JsonProperty("status") Status status) {
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

	public boolean passed() {
		return VERIFICATION_PASSED.equals(status);
	}

	protected enum Status {
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
