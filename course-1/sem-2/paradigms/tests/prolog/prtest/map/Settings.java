package prtest.map;

import base.ExtendedRandom;
import base.TestCounter;

import java.util.function.Function;

/**
 * Prolog test settings.

 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record Settings<K>(
        TestCounter counter,
        int size,
        int modifications,
        boolean sorted,
        boolean verbose,
        Function<ExtendedRandom, K> keyGenerator
) {
    public void log(final String name, final String format, final Object... args) {
        if (verbose) {
            counter.format("%15s %s%n", name, format.formatted(args));
        }
    }

    public void tick(final int i) {
        if (!verbose && i > 0 && i % 10 == 0) {
            counter.format("update %s of %s%n", i, modifications);
        }
    }

    public void run(final Runnable runnable) {
        counter.scope("size = " + size, runnable);
    }
}
