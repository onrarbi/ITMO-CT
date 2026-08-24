package prtest.tree;

import base.Selector;
import base.TestCounter;
import prtest.Rule;
import prtest.Value;
import prtest.map.MapChecker;
import prtest.map.State;

import java.util.Arrays;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static prtest.map.MapChecker.entryUpdater;
import static prtest.map.MapChecker.keyUpdater;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Search Trees</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class TreeTest {
    // 3637

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> putIfAbsent() {
        return MapChecker.entryUpdater("map_putIfAbsent", true, (state, key, value) -> {
            final Value old = state.expected.putIfAbsent(key, value);
            if (old != null) {
                state.values.remove(old);
                state.values.add(value);
            }
        });
    }

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> replace() {
        return MapChecker.entryUpdater("map_replace", false, (state, key, value) -> {
            final Value old = state.expected.replace(key, value);
            if (old != null) {
                state.values.remove(old);
                state.values.add(value);
            }
        });
    }


    // === 3839

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> subMapSize() {
        return MapChecker.biChecker(
                Rule.func("map_subMapSize", 3),
                State::randomKey,
                State::randomKey,
                (k1, k2) -> k1.compareTo(k2) < 0 ? map -> map.subMap(k1, k2).size() : map -> 0
        );
    }

    // === 3233

    private static <T, R> R extract(final T value, final Function<T, R> extractor) {
        return value == null ? null : extractor.apply(value);
    }

    private static <K extends Comparable<K>, T, R> Consumer<MapChecker<K, Void>> keyFunc(
            final String name,
            final BiFunction<NavigableMap<K, Value>, K, T> getter,
            final Function<T, R> extractor
    ) {
        return MapChecker.keyChecker(
                Rule.func("map_" + name, 2),
                (map, key) -> extract(getter.apply(map, key), extractor)
        );
    }

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> lowerKey() {
        return keyFunc("lowerKey", NavigableMap::lowerKey, Function.identity());
    }

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> lowerValue() {
        return keyFunc("lowerValue", NavigableMap::lowerEntry, Map.Entry::getValue);
    }

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> lowerEntry() {
        return keyFunc("lowerEntry", NavigableMap::lowerEntry, Function.identity());
    }


    // === 3435

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> removeLower() {
        return keyUpdater("map_removeLower", false, (state, key) -> {
            final K lower = state.expected.lowerKey(key);
            if (lower != null) {
                state.remove(lower);
            }
        });
    }

    private static <K extends Comparable<K>> Consumer<MapChecker<K, Void>> replaceLower() {
        return entryUpdater("map_replaceLower", false, (state, key, value) -> extract(
                state.expected.lowerEntry(key),
                e -> state.replace(e.getKey(), value)
        ));
    }


    // === Common code

    public static final Selector SELECTOR = new Selector(TreeTest.class, "easy", "hard")
            .variant("base", variant(tests -> {}))
            .variant("3637", variant(putIfAbsent(), replace()))
            .variant("3839", variant(putIfAbsent(), replace(), subMapSize()))
            .variant("3435", variant(lowerEntry(), replaceLower(), removeLower()))
            .variant("3233", variant(lowerKey(), lowerValue(), lowerEntry()))
            ;

    private TreeTest() {
    }

    @SafeVarargs
    /* package-private */ static Consumer<TestCounter> variant(final Consumer<MapChecker<Double, Void>>... adders) {
        return counter -> {
            final boolean hard = counter.mode() == 1;
            TreeTester.test(
                    counter, hard, true,
                    (random, range) -> random.nextInt(-range * 10, range * 10) / 10.0,
                    tests -> {
                        if (!hard) {
                            tests.disableUpdaters();
                        }
                        Arrays.stream(adders).forEachOrdered(adder -> adder.accept(tests));
                    }
            );
        };
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
