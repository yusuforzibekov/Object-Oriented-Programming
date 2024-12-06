package edu.epam.fop.jdbc.metadata.assertions;

import org.assertj.core.api.AbstractObjectAssert;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractDefinitionAssert<A extends AbstractDefinitionAssert<A, T>, T> extends AbstractObjectAssert<A, T> {

    public AbstractDefinitionAssert(T t, Class<?> selfType) {
        super(t, selfType);
    }

    protected A doesNotHaveProperty(String property, Function<T, ?> getter) {
        assertThat(getter.apply(actual))
                .withFailMessage("Expected not to have %s", property)
                .isNull();
        return myself;
    }

    protected A hasStringProperty(String property, Function<T, String> getter, String value) {
        assertThat(getter.apply(actual))
                .withFailMessage(failMessage(property, value))
                .isEqualToIgnoringCase(value);
        return myself;
    }

    protected A hasIntProperty(String property, Function<T, Integer> getter, int value) {
        assertThat(getter.apply(actual))
                .withFailMessage(failMessage(property, value))
                .isEqualTo(value);
        return myself;
    }

    protected A hasBooleanProperty(String property, Function<T, Boolean> getter, boolean value) {
        assertThat(getter.apply(actual))
                .withFailMessage(failMessage(property, value))
                .isEqualTo(value);
        return myself;
    }

    private String failMessage(String property, Object value) {
        return "Expected %s to have property %s = [%s]".formatted(actual, property, value);
    }

    protected <V, VA> A hasMap(Function<T, Map<String, V>> getter, Function<V, VA> valueAssertConst, Map<String, Consumer<VA>> assertions) {
            Map<String, V> columns = getter.apply(actual);
            assertThat(columns).containsOnlyKeys(assertions.keySet());
            assertions.forEach((name, assertion) -> hasMapValue(name, getter, valueAssertConst, assertion));
            return myself;
    }

    protected <V, VA> A hasMapValue(String name, Function<T, Map<String, V>> getter, Function<V, VA> valueAssertConst, Consumer<VA> assertion) {
        Map<String, V> map = getter.apply(actual);
        assertThat(map).containsKey(name);
        V value = map.get(name);
        assertion.accept(valueAssertConst.apply(value));
        return myself;
    }
}
