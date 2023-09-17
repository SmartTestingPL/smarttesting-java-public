package pl.smarttesting.verifier;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;

class SimpleObjectProvider<T> implements ObjectProvider<T> {
	private final T object;

	public SimpleObjectProvider(T object) {
		this.object = object;
	}

	public T getObject(Object... args) throws BeansException {
		return this.object;
	}

	public T getIfAvailable() throws BeansException {
		return this.object;
	}

	public T getIfUnique() throws BeansException {
		return this.object;
	}

	public T getObject() throws BeansException {
		return this.object;
	}
}