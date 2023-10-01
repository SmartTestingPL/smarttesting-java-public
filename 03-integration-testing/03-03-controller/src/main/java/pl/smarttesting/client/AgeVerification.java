package pl.smarttesting.client;

/**
 * Weryfikacja po wieku. Osoba w odpowiednim wieku zostanie
 * zweryfikowana pozytywnie.
 */
class AgeVerification implements Verification {

	@Override
	public boolean passes(Person person) {
		int age = person.getAge();
		if (age < 0) {
			throw new IllegalStateException("Age cannot be negative.");
		}
		return age >= 18 && age <= 99;
	}
}
