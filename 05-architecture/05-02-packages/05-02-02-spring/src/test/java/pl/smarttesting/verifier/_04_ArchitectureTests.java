package pl.smarttesting.verifier;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Przykład użycia ArchTest by upewnić się, że w części naszej aplikacji, gdzie podpinamy
 * framework, klasy z jednego pakietu nie są widoczne w innym pakiecie.
 */
@AnalyzeClasses(packagesOf = _04_ArchitectureTests.class)
public class _04_ArchitectureTests {

	/**
	 * Test weryfikujący, że żadne klasy z pakietu {@code pl.smarttesting.verifier.model} nie zależą
	 * od klas z pakietu {@code pl.smarttesting.verifier.infrastructure}
	 */
	@ArchTest
	public static final ArchRule should_not_contain_any_infrastructure_in_model_domain = noClasses()
			.that(resideInAPackage("..pl.smarttesting.verifier.model.."))
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..pl.smarttesting.verifier.infrastructure..");

}
