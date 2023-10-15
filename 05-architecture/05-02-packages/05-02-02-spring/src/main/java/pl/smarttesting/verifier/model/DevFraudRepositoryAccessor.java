package pl.smarttesting.verifier.model;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Deweloperski komponent, dzięki któremu wystawiamy kontroler REST,
 * poprzez który, za pomocą metody GET, jesteśmy w stanie zwrócić liczbę wpisów w bazie danych.
 */
@RestControllerEndpoint(id = "fraudrepo")
class DevFraudRepositoryAccessor {

	private final VerificationRepository repository;

	public DevFraudRepositoryAccessor(VerificationRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public long count() {
		return this.repository.count();
	}
}
