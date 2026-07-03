package com.example.lab;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.example.lab", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

	@ArchTest
	static final ArchRule controllers_must_not_access_repositories_directly = noClasses()
			.that().haveSimpleNameEndingWith("Controller")
			.should().dependOnClassesThat().haveSimpleNameEndingWith("Repository")
			.because("controllers should go through a service, not touch persistence directly");

	@ArchTest
	static final ArchRule repositories_must_only_be_used_by_services = classes()
			.that().haveSimpleNameEndingWith("Repository")
			.should().onlyHaveDependentClassesThat().haveSimpleNameEndingWith("Service")
			.because("repositories are an implementation detail of the service layer");

	@ArchTest
	static final ArchRule no_field_injection = noFields()
			.should().beAnnotatedWith(Autowired.class)
			.because("this codebase uses constructor injection everywhere");

	@ArchTest
	static final ArchRule controllers_are_annotated_correctly = classes()
			.that().haveSimpleNameEndingWith("Controller")
			.should().beAnnotatedWith(RestController.class);

	@ArchTest
	static final ArchRule services_are_annotated_correctly = classes()
			.that().haveSimpleNameEndingWith("Service")
			.and().areNotInterfaces()
			.should().beAnnotatedWith(Service.class);

	@ArchTest
	static final ArchRule repositories_are_interfaces = classes()
			.that().haveSimpleNameEndingWith("Repository")
			.should().beInterfaces()
			.andShould().notBeAnnotatedWith(Repository.class)
			.because("Spring Data repositories don't need @Repository, it's auto-detected");

	@ArchTest
	static final ArchRule domain_packages_are_free_of_cycles = slices()
			.matching("com.example.lab.(*)..")
			.should().beFreeOfCycles();
}
