package prtest.map;

import prtest.Rule;
import prtest.Value;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Prolog Map checker state.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class State<K extends Comparable<K>, M> {
    private static final Rule GET = Rule.func("map_get", 2);

    private final PrologMapTest<K> test;

    protected final Settings<K> settings;
    public final SetHistory<K> keys;
    public final SetHistory<Value> values;
    public NavigableMap<K, Value> expected;
    public Value actual;
    public final M model;

    public State(
            final PrologMapTest<K> test,
            final Settings<K> settings,
            final SetHistory<K> keys,
            final SetHistory<Value> values,
            final NavigableMap<K, Value> expected,
            final Value actual,
            final M model
    ) {
        this.test = test;
        this.settings = settings;
        this.keys = keys;
        this.values = values;
        this.expected = expected;
        this.actual = actual;
        this.model = model;
    }

    public void get(final K key) {
        test.assertResult(expected.get(key), GET, actual, key);
    }

    public K randomKey() {
        return keys.random();
    }

    public Value randomValue() {
        return values.random();
    }

    public void update(final Rule rule, final Object... args) {
        settings.log(rule.getName(), "(%s, V)", getArgs(args));
        actual = test.solveOne(rule.bind(0, actual), args).value();
    }

    public Value replace(final K key, final Value value) {
        final Value old = expected.replace(key, value);
        if (old != null) {
            values.remove(old);
            values.add(value);
        }
        return old;
    }

    public void remove(final K k) {
        final Value old = expected.remove(k);
        if (old != null) {
            keys.remove(k);
            values.remove(old);
        }
    }


    public <R> void assertRule(final Rule rule, final Function<NavigableMap<K, Value>, R> f, final Object... args) {
        final R result = f.apply(expected);
        if (result instanceof final Boolean bool) {
            settings.log(rule.getName(), "(%s)", getArgs(args));
            test.assertSuccess(bool, rule.bind(0, actual), args);
        } else {
            settings.log(rule.getName(), "(%s, R)", getArgs(args));
            test.assertResult(result, rule.bind(0, actual), args);
        }
    }

    private static String getArgs(final Object[] args) {
        return Stream.concat(Stream.of("map"), Arrays.stream(args).map(Objects::toString))
                .collect(Collectors.joining(", "));
    }

    public static <K extends Comparable<K>, M, T> BiConsumer<State<K, M>, T> updater(final BiFunction<NavigableMap<K, Value>, T, SortedMap<K, Value>> updater) {
        return (state, value) -> state.expected = new TreeMap<>(updater.apply(state.expected, value));
    }
}
