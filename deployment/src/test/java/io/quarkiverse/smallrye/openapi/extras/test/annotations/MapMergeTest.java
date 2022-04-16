package io.quarkiverse.smallrye.openapi.extras.test.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class MapMergeTest {

    @Test
    void testMerge() {
        final Object result = MyRequestBodyFilter.merge(Map.of("a", "o"), Map.of("a", "1"));

        assertThat(map(result)).containsEntry("a", "1");
    }

    @Test
    void testMergeOldNull() {
        final Object result = MyRequestBodyFilter.merge(null, Map.of("a", "1"));

        assertThat(result).isInstanceOf(Map.class);
        assertThat(map(result)).containsEntry("a", "1");
    }

    @Test
    void testMergeNewNull() {
        final Object result = MyRequestBodyFilter.merge(Map.of("a", "1"), null);

        assertThat(result).isNull();
    }

    @Test
    void testMergeL1() {
        final Object result = MyRequestBodyFilter.merge(Map.of("a", "1", "b", "2", "c", "3"), Map.of("b", "n"));

        assertThat(map(result)).containsEntry("b", "n");
    }

    @Test
    void testMergeL2() {
        final Object result = MyRequestBodyFilter.merge(Map.of("a", "1", "b", Map.of("ba", "21", "bb", "22"), "c", "3"),
                Map.of("b", Map.of("ba", "n1", "bc", "n2")));

        assertThat(map(result)).containsEntry("b", Map.of("ba", "n1", "bb", "22", "bc", "n2"));
    }

    private Map map(Object obj) {
        return Map.class.cast(obj);
    }
}
