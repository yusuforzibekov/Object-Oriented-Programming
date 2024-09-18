package edu.epam.fop.mocks;

import edu.epam.fop.mocks.client.ClientRepository;
import edu.epam.fop.mocks.client.ClientResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

class ComplianceTest {

    private static Factory factory;
    private static CtModel model;

    private static final Class<?> serviceClass = ClientService.class;
    private static final Class<?> repositoryClass = ClientRepository.class;

    @BeforeAll
    static void setUpClass() {
        Launcher spoon = new Launcher();
        spoon.addInputResource("src/main/java");
        factory = spoon.getFactory();
        model = spoon.buildModel();
    }

    @Test
    void shouldNotHaveAnyAnonymousClassDeclaration() {
        var anonymousClassDeclarations = model.getAllTypes().stream()
                .filter(type -> type.getReference().equals(factory.createCtTypeReference(serviceClass)))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No " + serviceClass.getSimpleName() + "class is found"))
                .getElements(el -> true)
                .stream()
                .filter(el -> el instanceof CtNewClass)
                .map(TestUtils::toSourceCodeReference)
                .toList();
        Assertions.assertThat(anonymousClassDeclarations)
                .withFailMessage(() -> String.format("Found anonymous class declarations in %s: %s",
                        serviceClass.getSimpleName(), anonymousClassDeclarations))
                .isEmpty();
    }

    @Test
    void shouldNotHaveAnyAnonymousClassInTheProject() {
        var anonymousTypes = model.getElements(new TypeFilter<>(CtType.class))
                .stream()
                .filter(CtTypeInformation::isAnonymous)
                .map(TestUtils::toSourceCodeReference)
//        .map(CtTypeInformation::getQualifiedName)
                .toList();
        Assertions.assertThat(anonymousTypes)
                .withFailMessage(() -> "Found anonymous types: " + anonymousTypes)
                .isEmpty();
    }

    @Test
    void shouldNotHaveAnyExplicitImplementationOfRepository() {
        var explicitImplementations = model.getElements(new TypeFilter<>(CtType.class))
                .stream()
                .filter(type -> !type.isInterface())
                .filter(type -> type.isSubtypeOf(factory.createCtTypeReference(repositoryClass)))
                .map(TestUtils::toSourceCodeReference)
                .toList();
        Assertions.assertThat(explicitImplementations)
                .withFailMessage(() -> String.format("Found explicit implementations of %s: %s",
                        repositoryClass.getSimpleName(), explicitImplementations))
                .isEmpty();
    }

    @Test
    void shouldUseMockito() {
        var mockitoIsUsed = model.getElements(new TypeFilter<>(CtClass.class))
                .stream()
                .filter(type -> type.isSubtypeOf(factory.createCtTypeReference(serviceClass)))
                .peek(System.out::println)
                .flatMap(type -> type.getReferencedTypes().stream())
                .filter(ref -> !ref.isPrimitive())
                .map(CtTypeReference::getPackage)
                .anyMatch(pack -> factory.Package().createReference(Mockito.class.getPackage()).equals(pack));
        Assertions.assertThat(mockitoIsUsed)
                .withFailMessage("Mockito library must be used, but none found")
                .isTrue();
    }

    @Test
    void shouldUseAnswerMockito() {
        var mockitoIsUsed = model.getElements(new TypeFilter<>(CtClass.class))
                .stream()
                .filter(type -> type.isSubtypeOf(factory.createCtTypeReference(serviceClass)))
                .flatMap(type -> type.getReferencedTypes().stream())
                .filter(ref -> !ref.isPrimitive())
                .anyMatch(className -> factory.Class().createReference(Answer.class.getName()).getSimpleName()
                        .equals(className.getSimpleName()));
        Assertions.assertThat(mockitoIsUsed)
                .withFailMessage("Mockito Answer class must be used, but none found")
                .isTrue();
    }

    @Test
    void shouldHaveOnlyReturnStatementInServiceSearchMethod() {
        var searchMethodCodeBlocks = model.getAllTypes()
                .stream()
                .filter(type -> type.isSubtypeOf(factory.createCtTypeReference(serviceClass)))
                .map(type -> type.getMethod(factory.Type().createReference(ClientResponse.class),
                        "search", factory.Type().LONG_PRIMITIVE))
                .flatMap(method -> method.getElements(el -> el instanceof CtBlock).stream())
                .map(CtBlock.class::cast)
                .toList();
        Assertions.assertThat(searchMethodCodeBlocks)
                .withFailMessage("search method must not have any statement except for return")
                .hasSize(1);
        var statements = searchMethodCodeBlocks.iterator().next().getStatements();
        Assertions.assertThat(statements)
                .withFailMessage("search method must not have any statement except for return")
                .hasSize(1)
                .hasOnlyElementsOfType(CtReturn.class);
        Assertions.assertThat(statements.iterator().next().toString())
                .isEqualTo("return client.findById(id)");
    }
}
