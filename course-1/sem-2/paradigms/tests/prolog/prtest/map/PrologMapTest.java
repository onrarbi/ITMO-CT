package prtest.map;

import base.ExtendedRandom;
import base.TestCounter;
import prtest.PrologTest;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Common tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Map</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class PrologMapTest<K extends Comparable<K>> extends PrologTest {
    private final boolean updates;
    private final boolean sorted;
    private final MapChecker<K, ?> test;
    private final BiFunction<ExtendedRandom, Integer, K> keyGenerator;

    public PrologMapTest(
            final TestCounter counter,
            final boolean updates,
            final boolean sorted,
            final Path file,
            final Function<PrologMapTest<K>, MapChecker<K, ?>> testFactory,
            final BiFunction<ExtendedRandom, Integer, K> keyGenerator
    ) {
        super(counter, file);
        this.updates = updates;
        this.sorted = sorted;
        test = testFactory.apply(this);
        this.keyGenerator = keyGenerator;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + updates + ")";
    }

    @Override
    public void test() {
        for (int i = 0; i < 10; i++) {
            check(i, 10, 10, true);
        }
        check(100, 10000, 100, false);
        check(200, 10000, 20, false);
        check(500 / TestCounter.DENOMINATOR2, 100000, 0, false);
    }

    private void check(final int size, final int range, final int modifications, final boolean verbose) {
        test.check(new Settings<>(counter, size, modifications, sorted, verbose, random -> keyGenerator.apply(random, range)));
    }

    protected void check(final Runnable check) {
        counter.test(check);
    }

    public static <K extends Comparable<K>, M> void test(
            final TestCounter counter,
            final Path file,
            final boolean updates,
            final boolean sorted,
            final Consumer<MapChecker<K, M>> addTests,
            final BiFunction<ExtendedRandom, Integer, K> keyGenerator,
            final Function<PrologMapTest<K>, MapChecker<K, M>> testFactory
    ) {
        final Function<PrologMapTest<K>, MapChecker<K, ?>> newFactory = test -> {
            final MapChecker<K, M> tests = testFactory.apply(test);
            addTests.accept(tests);
            return tests;
        };
        new PrologMapTest<>(counter, updates, sorted, file, newFactory, keyGenerator).run(PrologMapTest.class);
    }
}
