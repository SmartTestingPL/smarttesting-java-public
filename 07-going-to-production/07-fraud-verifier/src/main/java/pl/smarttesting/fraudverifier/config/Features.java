package pl.smarttesting.fraudverifier.config;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;

/**
 * Enum implementujący {@link Feature} z biblioteki togglz. Umożliwia przestawienie aplikacji
 * na stosowanie nowej logiki weryfikacji. Domyślnie nieaktywny.
 */
public enum Features implements Feature {

	// Etykieta pozwala na określenie jak feature będzie wyświetlany w GUI
	@Label("New Verification Feature")
	NEW_VERIFICATION

}
