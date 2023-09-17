package pl.smarttesting.bik.score.utils;

import pl.smarttesting.bik.score.domain.Pesel;

/**
 * @author Olga Maciaszek-Sharma
 */
public final class TestUtils {

	private TestUtils() {
		throw new IllegalStateException("Should not instantiate utility class");
	}

	public static Pesel anId() {
		return new Pesel("96082812079");
	}
}
