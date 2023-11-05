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
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import static pl.smarttesting.verifier.model.CustomerVerificationResult.failed;

/**
 * Weryfikacja czy klient jest oszustem czy nie. Przechodzi po
 * różnych implementacjach weryfikacji i jeśli, przy którejś okaże się,
 * że użytkownik jest oszustem, wówczas odpowiedni rezultat zostanie zwrócony.
 */
class _01_CustomerVerifier implements Closeable {

	private final Set<Verification> verifications;

	private final ExecutorService executor = Executors.newCachedThreadPool();

	private final ExecutorCompletionService<VerificationResult> executorService = new ExecutorCompletionService<>(executor);

	private final FraudAlertNotifier fraudAlertNotifier;

	public _01_CustomerVerifier(Set<Verification> verifications, FraudAlertNotifier fraudAlertNotifier) {
		this.verifications = verifications;
		this.fraudAlertNotifier = fraudAlertNotifier;
	}

	public _01_CustomerVerifier(Set<Verification> verifications) {
		this.verifications = verifications;
		this.fraudAlertNotifier = null;
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

	/**
	 * Wykonuje weryfikacje w wielu wątkach.
	 * Nie pozwala rzucić wyjątku w przypadku błędu w procesowaniu.
	 * @param customer - klient do zweryfikowania
	 * @return rezultaty w kolejności ukończenia
	 */
	List<VerificationResult> verifyNoException(Customer customer) {
		verifications
				// zacznij wykonywać wywołania równolegle
				.forEach(verification ->
						executeInANewThread(customer, verification));
		// zwróć listę odpowiedzi w kolejności ukończenia
		return resultsInOrderOfCompletionWithNoException();
	}

	/**
	 * Wykonuje weryfikacje w sposób reaktywny.
	 * @param customer - klient do zweryfikowania
	 * @return rezultaty w kolejności ukończenia
	 */
	Flux<VerificationResult> verifyFlux(Customer customer) {
		return Flux.fromIterable(verifications)
				.subscribeOn(Schedulers.parallel())
				.map(v -> v.passes(customer.getPerson()));
	}

	/**
	 * Wykonuje weryfikacje w sposób reaktywny i wielowątkowy.
	 * @param customer - klient do zweryfikowania
	 * @return rezultaty w kolejności ukończenia
	 */
	ParallelFlux<VerificationResult> verifyParallelFlux(Customer customer) {
		return Flux.fromIterable(verifications)
				.parallel(3)
				.runOn(Schedulers.parallel())
				.map(v -> v.passes(customer.getPerson()));
	}

	private List<VerificationResult> resultsInOrderOfCompletion() {
		return verifications.stream()
				.map(v -> take())
				.map(this::result)
				.collect(Collectors.toList());
	}

	private List<VerificationResult> resultsInOrderOfCompletionWithNoException() {
		return verifications.stream()
				.map(v -> new VerificationAndFuture(v, take()))
				.map(this::resultNoException)
				.collect(Collectors.toList());
	}

	private Future<VerificationResult> executeInANewThread(Customer customer, Verification verification) {
		return executorService.submit(
				() -> verification.passes(customer.getPerson()));
	}

	/**
	 * Dokonuje weryfikacji w osobnych wątkach.
	 * @param customer - klient do zweryfikowania
	 */
	void verifyAsync(Customer customer) {
		verifications
				.forEach(verification ->
						executeInANewThread(customer, verification));
	}

	/**
	 * Wysyła notyfikację o znalezionym oszuście.
	 * @param customer - klient do zweryfikowania
	 */
	void foundFraud(Customer customer) {
		fraudAlertNotifier
				.fraudFound(new CustomerVerification(customer.getPerson(), failed(customer.getUuid())));
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

	private VerificationResult resultNoException(VerificationAndFuture vf) {
		try {
			Future<VerificationResult> future = vf.future;
			return future.get(5, TimeUnit.SECONDS);
		}
		catch (Exception e) {
			return new VerificationResult(vf.verification.name(), false);
		}
	}

	@Override
	public void close() throws IOException {
		executor.shutdown();
	}
}

class VerificationAndFuture {
	final Verification verification;
	final Future<VerificationResult> future;

	VerificationAndFuture(Verification verification, Future<VerificationResult> future) {
		this.verification = verification;
		this.future = future;
	}
}