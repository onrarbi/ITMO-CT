package prtest.map;

import base.ExtendedRandom;
import base.Functional;
import base.Pair;
import prtest.Rule;
import prtest.Value;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Common Prolog Map checking code.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class MapChecker<K extends Comparable<K>, M> {
    private final PrologMapTest<K> test;

    private final Map<String, Consumer<State<K, M>>> checkers = new LinkedHashMap<>();
    private final Map<String, Consumer<State<K, M>>> easy = new LinkedHashMap<>();
    private final Map<String, Consumer<State<K, M>>> hard = new LinkedHashMap<>();
    private final Function<List<Entry<K>>, M> modelFactory;
    private final Function<List<Entry<K>>, Value> actualFactory;
    private boolean allowUpdates = true;

    public MapChecker(
            final PrologMapTest<K> test,
            final Function<List<Entry<K>>, M> modelFactory,
            final Function<List<Entry<K>>, Value> actualFactory,
            final TriConsumer<M, K, Value> modelPut,
            final BiConsumer<M, K> modelRemove,
            final Consumer<State<K, M>> checker
    ) {
        this.test = test;
        this.modelFactory = modelFactory;
        this.actualFactory = actualFactory;

        checker(Rule.pred("postChecker", 0), checker);

        MapChecker.<K, M>compose(
                entryUpdater("map_put", true, (state, key, value) -> {
                    state.expected.put(key, value);
                    modelPut.update(state.model, key, value);
                    state.keys.add(key);
                    state.values.add(value);
                }),
                keyUpdater("map_remove", false, (state, key) -> {
                    final Value value = state.expected.remove(key);
                    modelRemove.accept(state.model, key);
                    state.keys.remove(key);
                    state.values.remove(value);
                })
        ).accept(this);
    }

    /* package-private*/ void check(final Settings<K> settings) {
        settings.run(() -> {
            final SetHistory<K> keys = new SetHistory<>(() -> settings.keyGenerator().apply(test.random()), test.random());
            final SetHistory<Value> values = new SetHistory<>(() -> Value.string(test.random().randomString(ExtendedRandom.ENGLISH)), test.random());
            final List<Entry<K>> entries = Stream.generate(() -> new Entry<>(keys.uniqueAndAdd(), values.uniqueAndAdd()))
                    .limit(settings.size())
                    .collect(Collectors.toList());

            if (settings.sorted()) {
                entries.sort(Comparator.comparing(Entry::getKey));
            }

            settings.log("build", "%s", entries);

            final State<K, M> state = new State<>(
                    test,
                    settings,
                    keys,
                    values,
                    new TreeMap<>(entries.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue))),
                    actualFactory.apply(entries),
                    modelFactory.apply(entries)
            );

            check(state);

            final List<Map.Entry<String, Consumer<State<K, M>>>> modifiers =
                    Functional.concat(easy.entrySet(), allowUpdates ? hard.entrySet() : List.of());
            if (!modifiers.isEmpty()) {
                for (int i = 1; i <= settings.modifications(); i++) {
                    settings.tick(i);
                    final Map.Entry<String, Consumer<State<K, M>>> modifier = test.random().randomItem(modifiers);
                    modifier.getValue().accept(state);
                    check(state);
                }
            }
        });
    }

    private void check(final State<K, M> state) {
        checkers.values().forEach(checker -> test.check(() -> checker.accept(state)));
    }

    private void checker(final Rule rule, final Consumer<State<K, M>> checker) {
        checkers.put(rule.getName(), checker);
    }

    private static <K extends Comparable<K>, M, A, R> Consumer<MapChecker<K, M>> checker(
            final Rule rule,
            final Function<State<K, M>, A> gen,
            final Function<A, List<Object>> getArgs,
            final BiFunction<NavigableMap<K, Value>, A, R> f
    ) {
        return test -> test.checker(rule, state -> {
            final A a = gen.apply(state);
            state.assertRule(rule, map -> f.apply(map, a), getArgs.apply(a).toArray());
        });
    }

    public static <K extends Comparable<K>, M, R> Consumer<MapChecker<K, M>> noneChecker(
            final Rule rule,
            final Function<NavigableMap<K, Value>, R> f
    ) {
        return checker(rule, state -> null, none -> List.of(), (state, none) -> f.apply(state));
    }

    public static <K extends Comparable<K>, M, F, S, R> Consumer<MapChecker<K, M>> biChecker(
            final Rule rule,
            final Function<State<K, M>, F> gen1,
            final Function<State<K, M>, S> gen2,
            final BiFunction<F, S, Function<NavigableMap<K, Value>, R>> f
    ) {
        return checker(
                rule,
                state -> Pair.of(gen1.apply(state), gen2.apply(state)),
                pair -> List.of(pair.first(), pair.second()),
                (model, pair) -> f.apply(pair.first(), pair.second()).apply(model)
        );
    }

    public static <K extends Comparable<K>, M, T, R> Consumer<MapChecker<K, M>> checker(
            final Rule rule,
            final Function<State<K, M>, T> gen,
            final BiFunction<NavigableMap<K, Value>, T, R> f
    ) {
        return checker(rule, gen, List::of, f);
    }

    public static <K extends Comparable<K>, M, R> Consumer<MapChecker<K, M>> keyChecker(
            final Rule rule,
            final BiFunction<NavigableMap<K, Value>, K, R> f
    ) {
        return checker(rule, State::randomKey, f);
    }

    @SafeVarargs
    public static <K extends Comparable<K>, M> Consumer<MapChecker<K, M>> compose(final Consumer<MapChecker<K, M>>... variants) {
        return test -> Arrays.stream(variants).forEachOrdered(variant -> variant.accept(test));
    }

    private static <K extends Comparable<K>, M, T> Consumer<MapChecker<K, M>> updater(
            final String ruleName,
            final int arity,
            final boolean hard,
            final Function<State<K, M>, T> generator,
            final Function<T, List<Object>> toArgs,
            final BiConsumer<State<K, M>, T> updater
    ) {
        final Rule rule = Rule.func(ruleName, arity + 1);
        return test -> (hard ? test.hard : test.easy).put(ruleName, state -> {
            final T args = generator.apply(state);
            updater.accept(state, args);
            state.update(rule, toArgs.apply(args).toArray());
        });
    }

    public static <K extends Comparable<K>, M, F, S> Consumer<MapChecker<K, M>> biUpdater(
            final String rule,
            final boolean hard,
            final Function<State<K, M>, F> gen1,
            final Function<State<K, M>, S> gen2,
            final TriConsumer<State<K, M>, F, S> updater
    ) {
        return updater(
                rule, 2, hard,
                state -> Pair.of(gen1.apply(state), gen2.apply(state)),
                pair -> List.of(pair.first(), pair.second()),
                (state, pair) -> updater.update(state, pair.first(), pair.second())
        );
    }

    public static <K extends Comparable<K>, M> Consumer<MapChecker<K, M>> entryUpdater(
            final String rule,
            final boolean hard,
            final TriConsumer<State<K, M>, K, Value> updater
    ) {
        return biUpdater(rule, hard, State::randomKey, State::randomValue, updater);
    }

    public static <K extends Comparable<K>, M> Consumer<MapChecker<K, M>> keyUpdater(
            final String rule,
            final boolean hard,
            final BiConsumer<State<K, M>, K> updater
    ) {
        return updater(rule, 1, hard, State::randomKey, List::of, updater);
    }

    public static <K extends Comparable<K>, M> Consumer<MapChecker<K, M>> valueUpdater(
            final String rule,
            final boolean hard,
            final BiConsumer<State<K, M>, Value> updater
    ) {
        return updater(rule, 1, hard, State::randomValue, List::of, updater);
    }

    public static <M, K extends Comparable<K>> Consumer<MapChecker<K, M>> noneUpdater(
            final String rule,
            final boolean hard,
            final Consumer<State<K, M>> updater
    ) {
        return updater(
                rule, 0, hard,
                state -> null,
                none -> List.of(),
                (state, none) -> updater.accept(state)
        );
    }

    public void disableUpdaters() {
        allowUpdates = false;
    }

    public interface TriConsumer<S, T, U> {
        void update(S s, T t, U u);
    }
}
