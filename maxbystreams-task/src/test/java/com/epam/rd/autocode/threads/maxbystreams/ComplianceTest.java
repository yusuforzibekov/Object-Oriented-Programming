package com.epam.rd.autocode.threads.maxbystreams;

import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ComplianceTest {

    private static final String CLASS_NAME = MaxOfMatrix.class.getName();

    static CtModel ctModel = init();

    static CtModel init() {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("src/main/java/");
        return ctModel = spoon.buildModel();
    }

    @Test
    void testCompliance() {
        assertTrue(isImplemented("oneThreadSearch") &&
                        isImplemented("multipleThreadSearch"),
                "Must be implemented");
        testNoMoreClasses();
        testNoMoreFields();
        testNoMoreMethods();
        testNoMoreThan2Statement("oneThreadSearch");
        testNoMoreThan2Statement("multipleThreadSearch");
    }

    // no more classes
    void testNoMoreClasses() {
        List<String> types = ctModel.filterChildren((Filter<CtType<?>>) el ->
                        !el.getQualifiedName().equals(CLASS_NAME))
                .map((CtType<?> el) -> el.getQualifiedName())
                .list();
        assertEquals(0, types.size(),
                "You must not add any other classes.");
    }

    // no more classes
    void testNoMoreFields() {
        CtType<?> type = ctModel.filterChildren((Filter<CtType<?>>) el ->
                        el.getQualifiedName().equals(CLASS_NAME))
                .first();
        List<CtField<?>> fields = type.filterChildren((Filter<CtField<?>>) el -> true).list();
        assertEquals(0, fields.size(),
                "You must not add any fields.");
    }

    // no more methods
    void testNoMoreMethods() {
        List<CtMethod<?>> methods = ctModel.filterChildren((Filter<CtType<?>>) el ->
                        el.getQualifiedName().equals(CLASS_NAME))
                .filterChildren((CtMethod<?> m) -> m.getParent(new TypeFilter<>(CtType.class))
                        .getQualifiedName().equals(CLASS_NAME))
                .list();
        List<Object> allowedMethods = List.of("pause", "oneThreadSearch", "multipleThreadSearch");
        List<String> actual = methods.stream()
                .map(CtMethod::getSimpleName)
                .filter(o -> !allowedMethods.contains(o))
                .sorted()
                .toList();
        assertTrue(actual.isEmpty(),
                "You can not add any additional methods, but found: " + actual);
    }

    // no more than 2 statement
    void testNoMoreThan2Statement(String methodName) {
        CtMethod<?> method = ctModel.filterChildren((CtClass<?> el) ->
                        el.getQualifiedName()
                                .equals(CLASS_NAME))
                .filterChildren((CtMethod<?> el) -> {
                    String simpleName = el.getSimpleName();
                    return simpleName.equals(methodName);
                })
                .first();
        assertNotNull(method, "The method must be present: " + methodName);
        List<CtStatement> statements = method.getBody().getStatements()
                .stream()
                .filter((CtStatement st) -> !(st instanceof CtReturn<?>))
                .toList();
        assertEquals(1, statements.size(),
                "You can develop the method using only two statement " +
                        "and one of them must be 'return ...'. " +
                        "Found except 'return ...':\n" + statementsToString(statements));
    }

    // is implemented
    static boolean isImplemented(String methodName) {
        CtReturn<?> method = ctModel.filterChildren((CtClass<?> el) ->
                        el.getQualifiedName()
                                .equals(CLASS_NAME))
                .filterChildren((CtMethod<?> el) -> {
                    String simpleName = el.getSimpleName();
                    return simpleName.equals(methodName);
                })
                .filterChildren((CtBlock<?> body) -> true)
                .filterChildren((CtReturn<?> st) -> true)
                .first();
        return !method.toString().contains("return null");
    }

    String statementsToString(List<CtStatement> statements) {
        return statements.stream()
                .map(Objects::toString)
                .collect(Collectors.joining("'\n~ '", "~ '", "'\n"));
    }
}
