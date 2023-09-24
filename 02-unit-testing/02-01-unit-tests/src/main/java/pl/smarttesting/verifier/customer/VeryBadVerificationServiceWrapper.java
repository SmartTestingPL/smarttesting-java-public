package pl.smarttesting.verifier.customer;

/**
 * Klasa "wrapper" otaczająca statyczną metodę, która realizuje jakieś ciężkie operacje bazodanowe.
 * Nie polecamy robienia czegoś takiego w metodzie statycznej, ale tu pokazujemy jak to obejść i przetestować
 * jeżeli z jakiegoś powodu nie da się tego zmienić (np. metoda statyczna jest dostarczana przez kogoś innego).
 * @see    VeryBadVerificationService
 */
public class VeryBadVerificationServiceWrapper {

	public boolean verify() {
		return VeryBadVerificationService.runHeavyQueriesToDatabaseFromStaticMethod();
	}
}
