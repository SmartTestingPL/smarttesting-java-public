package pl.smarttesting;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;

/**
 * Klasa testowa wykorzystująca narzędzie PowerMock, do mockowania
 * wywołań metod statycznych.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StringUtils.class, DatabaseAccessor.class})
public class AppTest {

	/**
	 * W tym teście mockujemy wszystko co się da. Włącznie z mockowaniem list.
	 * Mockujemy też wywołanie metody z klasy narzędziowej - StringUtils.
	 */
	@Test
	public void should_find_any_empty_name() {
		List<String> names = mock(List.class);
		Iterator iterator = mock(Iterator.class);
		BDDMockito.given(names.iterator()).willReturn(iterator);
		BDDMockito.given(iterator.hasNext()).willReturn(true, false);
		BDDMockito.given(iterator.next()).willReturn("");
		PowerMockito.mockStatic(StringUtils.class);
		PowerMockito.when(StringUtils.isEmpty(BDDMockito.anyString())).thenReturn(true);

		Assert.assertTrue(new _03_FraudService().anyNameIsEmpty(names));
	}

	/**
	 * Poprawiona wersja testu powyżej.
	 * - Nie mockujemy listy - tworzymy ją.
	 * - Nie mockujemy wywołań metody statycznej z biblioteki zewnętrznej.
	 */
	@Test
	public void should_find_any_empty_name_fixed() {
		List<String> names = Arrays.asList("non empty", "");

		Assert.assertThat(new _03_FraudService().anyNameIsEmpty(names), is(true));
	}

	/**
	 * Przykład opakowania kodu, wywołującego metodę statyczną łączącą się do bazy danych.
	 */
	@Test
	public void should_do_some_work_in_database_when_empty_string_found() {
		DatabaseAccessorWrapper wrapper = mock(DatabaseAccessorWrapper.class);
		List<String> names = Arrays.asList("non empty", "");

		new FraudServiceFixed(wrapper).anyNameIsEmpty(names);

		BDDMockito.then(wrapper).should().storeInDatabase();
	}
}

/**
 * Klasa, w której wołamy metody statyczne.
 */
class _03_FraudService {

	boolean anyNameIsEmpty(List<String> names) {
		for (String name : names) {
			if (StringUtils.isEmpty(name)) {
				DatabaseAccessor.storeInDatabase();
				return true;
			}
		}
		return false;
	}
}

/**
 * Poprawiona implementacja, gdzie zamiast wywołania statycznego {@link DatabaseAccessor}
 * wywołujemy naszą wersję {@link DatabaseAccessorWrapper}.
 */
class FraudServiceFixed {
	private final DatabaseAccessorWrapper wrapper;

	FraudServiceFixed(DatabaseAccessorWrapper wrapper) {
		this.wrapper = wrapper;
	}

	boolean anyNameIsEmpty(List<String> names) {
		for (String name : names) {
			if (StringUtils.isEmpty(name)) {
				wrapper.storeInDatabase();
				return true;
			}
		}
		return false;
	}
}

/**
 * Nasza klasa opakowująca wywołanie metody statycznej.
 */
class DatabaseAccessorWrapper {
	void storeInDatabase() {
		DatabaseAccessor.storeInDatabase();
	}
}

/**
 * Klasa symulująca dostęp do bazy danych.
 */
class DatabaseAccessor {
	public static void storeInDatabase() {

	}
}