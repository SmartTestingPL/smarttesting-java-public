package pl.smarttesting.verifier;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = ArchitectureTests.class)
public class ArchitectureTests {

	@ArchTest
	public static final ArchRule should_not_contain_any_infrastructure_in_model_domain = noClasses()
			.that(resideInAPackage("..pl.smarttesting.verifier.model.."))
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..pl.smarttesting.verifier.infrastructure..");

}
