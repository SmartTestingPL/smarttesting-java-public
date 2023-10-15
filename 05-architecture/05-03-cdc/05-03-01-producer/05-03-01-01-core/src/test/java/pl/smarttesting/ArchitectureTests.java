package pl.smarttesting;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = ArchitectureTests.class)
public class ArchitectureTests {

	@ArchTest
	public static final ArchRule should_not_contain_any_spring_reference_in_core_domain = noClasses()
			.should()
			.dependOnClassesThat()
			.resideInAPackage("..org.springframework..");

}
