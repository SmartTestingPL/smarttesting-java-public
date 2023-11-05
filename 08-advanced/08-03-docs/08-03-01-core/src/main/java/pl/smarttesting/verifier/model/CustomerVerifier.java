package pl.smarttesting.verifier.model;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import pl.smarttesting.customer.Customer;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
class CustomerVerifier implements Closeable {

	private final Set<Verification> verifications;

	private final ExecutorService executor = Executors.newCachedThreadPool();

	private final ExecutorCompletionService<VerificationResult> executorService = new ExecutorCompletionService<>(executor);

	public CustomerVerifier(Set<Verification> verifications) {
		this.verifications = verifications;
	}

	/**
	 * Wykonuje weryfikacje w wielu wątkach.
	 * @param customer - klient do zweryfikowania
	 * @return rezultaty w kolejności ukończenia
	 */
	List<VerificationResult> verify(Customer customer) {
		verifications
				// zacznij wykonywać wywołania równolegle
				.forEach(verification ->
						executeInANewThread(customer, verification));
		// zwróć listę odpowiedzi w kolejności ukończenia
		return resultsInOrderOfCompletion();
	}

	private List<VerificationResult> resultsInOrderOfCompletion() {
		return verifications.stream()
				.map(v -> take())
				.map(this::result)
				.collect(Collectors.toList());
	}

	private Future<VerificationResult> executeInANewThread(Customer customer, Verification verification) {
		return executorService.submit(
				() -> verification.passes(customer.getPerson()));
	}

	private Future<VerificationResult> take() {
		try {
			return executorService.take();
		}
		catch (InterruptedException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private VerificationResult result(Future<VerificationResult> f) {
		try {
			return f.get(5, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void close() throws IOException {
		executor.shutdown();
	}
}