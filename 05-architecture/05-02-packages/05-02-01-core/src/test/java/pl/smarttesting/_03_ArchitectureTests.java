package pl.smarttesting;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Przykład użycia ArchTest by upewnić się, że w głównej części naszej domeny
 * nie mamy klas związanych z frameworkiem (np. Springiem).
 */
@AnalyzeClasses(packagesOf = _03_ArchitectureTests.class)
public class _03_ArchitectureTests {

	/**
	 * Test weryfikujący, że żadna klasa z tego modułu nie zależy od klasy z pakietu frameworka Spring.
	 */
	@ArchTest
	public static final ArchRule should_not_contain_any_spring_reference_in_core_domain = noClasses()
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..org.springframework..");

}
