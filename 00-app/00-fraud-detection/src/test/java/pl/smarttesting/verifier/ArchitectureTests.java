package pl.smarttesting.verifier;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// Dotyczy lekcji 05-02
@AnalyzeClasses(packagesOf = ArchitectureTests.class)
public class ArchitectureTests {

	@ArchTest
	public static final ArchRule should_not_contain_any_spring_reference_in_verifications = noClasses()
			.that(resideInAPackage("pl.smarttesting.verifier.verification"))
			.and(simpleNameEndingWith("Verification"))
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..org.springframework..");

}
