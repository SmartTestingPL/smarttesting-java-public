package pl.smarttesting.verifier.model.verification;

/**
 * Wyjątek domenowy.
 */
class _03_DomainException extends RuntimeException {
	_03_DomainException() {
	}

	_03_DomainException(String message) {
		super(message);
	}

	_03_DomainException(String message, Throwable cause) {
		super(message, cause);
	}

	_03_DomainException(Throwable cause) {
		super(cause);
	}

	_03_DomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
