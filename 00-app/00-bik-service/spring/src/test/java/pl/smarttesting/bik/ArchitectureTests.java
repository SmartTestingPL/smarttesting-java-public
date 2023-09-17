package pl.smarttesting.bik;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

// Dotyczy lekcji 05-02
@AnalyzeClasses(packagesOf = ArchitectureTests.class)
public class ArchitectureTests {

	@ArchIgnore(reason = "Kod produkcyjny jest zle napisany - ten test ma to wychwycić")
	@ArchTest
	public static final ArchRule should_not_contain_any_infrastructure_in_model_domain = noClasses()
			.that(resideOutsideOfPackage("pl.smarttesting.bik.score.infrastructure"))
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..pl.smarttesting.bik.score.infrastructure..");

}
