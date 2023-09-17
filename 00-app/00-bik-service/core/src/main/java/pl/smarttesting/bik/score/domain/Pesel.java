package pl.smarttesting.bik.score.domain;

import java.util.Objects;

public class Pesel {
	public String pesel;

	public Pesel(String pesel) {
		if (pesel.length() != 11) {
			throw new IllegalArgumentException("PESEL must be of 11 chars");
		}
		this.pesel = pesel;
	}
	
	public Pesel() {
	}

	public String getPesel() {
		return pesel;
	}

	public void setPesel(String pesel) {
		this.pesel = pesel;
	}

	public String obfuscated() {
		return this.pesel.substring(7);
	}

	@Override
	public String toString() {
		return obfuscated();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Pesel pesel1 = (Pesel) o;
		return Objects.equals(pesel, pesel1.pesel);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pesel);
	}
}
