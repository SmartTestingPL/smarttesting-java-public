package pl.smarttesting.verifier.verification;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.smarttesting.customer.Person;
import pl.smarttesting.verifier.Verification;

/**
 * Weryfikacja po numerze PESEL. Osoba z odpowiednim numerem PESEL
 * zweryfikowana pozytywnie. Algorytm napisany na podstawie https://obywatel.gov.pl/pl/dokumenty-i-dane-osobowe/czym-jest-numer-pesel.
 *
 *
 * Co oznaczają poszczególne cyfry w numerze PESEL
 *
 * Każda z 11 cyfr w numerze PESEL ma swoje znaczenie. Można je podzielić następująco:
 *
 * RRMMDDPPPPK
 *
 * RR - to 2 ostanie cyfry roku urodzenia,
 *
 * MM - to miesiąc urodzenia (zapoznaj się z sekcją  "Dlaczego osoby urodzone po 1999 roku mają inne oznaczenie miesiąca urodzenia", która znajduje się poniżej),
 *
 * DD - to dzień urodzenia,
 *
 * PPPP - to liczba porządkowa oznaczająca płeć. U kobiety ostatnia cyfra tej liczby jest parzysta (0, 2, 4, 6, 8), a u mężczyzny - nieparzysta (1, 3, 5, 7, 9),
 *
 * K - to cyfra kontrolna.
 *
 * Przykład: PESEL 810203PPP6K należy do kobiety, która urodziła się 3 lutego 1981 roku, a PESEL 761115PPP3K - do mężczyzny, który urodził się 15 listopada 1976 roku.
 */
public class IdentificationNumberVerification implements Verification {

	private static final Logger log = LoggerFactory.getLogger(IdentificationNumberVerification.class);

	@Override
	public boolean passes(Person person) {
		boolean passed = genderMatchesIdentificationNumber(person)
				&& identificationNumberStartsWithDateOfBirth(person)
				&& identificationNumberWeightIsCorrect(person);
		log.info("Person [{}] passed the id number check [{}]", person, passed);
		return passed;
	}


	private boolean genderMatchesIdentificationNumber(Person person) {
		if (Integer.parseInt(person.getNationalIdentificationNumber()
				.substring(9, 10)) % 2 == 0) {
			return Person.GENDER.FEMALE.equals(person.getGender());
		}
		else {
			return Person.GENDER.MALE.equals(person.getGender());
		}
	}

	private boolean identificationNumberStartsWithDateOfBirth(Person person) {
		String dateOfBirthString = person.getDateOfBirth()
				.format(DateTimeFormatter.ofPattern("yyMMdd"));
		if (dateOfBirthString.charAt(0) == '0') {
			int monthNum = Integer.parseInt(dateOfBirthString.substring(2, 4));
			monthNum += 20;
			dateOfBirthString = dateOfBirthString
					.substring(0, 2) + monthNum + dateOfBirthString.substring(4, 6);
		}
		return dateOfBirthString
				.equals(person.getNationalIdentificationNumber().substring(0, 6));
	}

	/**
	 * Pomnóż każdą cyfrę z numeru PESEL przez odpowiednią wagę: 1-3-7-9-1-3-7-9-1-3.
	 */
	private boolean identificationNumberWeightIsCorrect(Person person) {
		int[] weights = {1, 2, 7, 8, 1, 3, 7, 9, 1, 3};

		if (person.getNationalIdentificationNumber().length() != 11) {
			return false;
		}

		// Dodaj do siebie otrzymane wyniki. Uwaga, jeśli w trakcie mnożenia otrzymasz liczbę dwucyfrową, należy dodać tylko ostatnią cyfrę (na przykład zamiast 63 dodaj 3).
		int weightSum = 0;
		for (int i = 0; i < 10; i++) {
			weightSum += Integer.parseInt(person.getNationalIdentificationNumber()
					.substring(i, i + 1)) * weights[i];
		}

		int actualSum = (10 - weightSum % 10) % 10;

		// Odejmij uzyskany wynik od 10. Uwaga: jeśli w trakcie dodawania otrzymasz liczbę dwucyfrową, należy odjąć tylko ostatnią cyfrę (na przykład zamiast 32 odejmij 2). Cyfra, która uzyskasz, to cyfra kontrolna. 10 - 2 = 8
		int checkSum = Integer
				.parseInt(person.getNationalIdentificationNumber().substring(10, 11));

		return actualSum == checkSum;
	}
}
