package prtest.primes;

import base.Selector;
import base.TestCounter;
import prtest.Rule;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#prolog-map">Prolog Primes</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class PrimesTest {
    // === 3637
    private static void compact(final PrimesTester t) {
        t.checkDivisorsG(
                Rule.pred("compact_prime_divisors", 2),
                true,
                divisors -> Compact.of(divisors).toList()
        );
    }

    public record Compact(int p, int n) {
        public static Stream<Compact> of(final IntStream divisors) {
            return divisors.boxed()
                    .collect(Collectors.groupingBy(
                            Function.identity(),
                            TreeMap::new,
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .map(e -> new Compact(e.getKey(), e.getValue().intValue()));
        }
    }

    // === 3839
    private static void divisorsDivisors(final PrimesTester t) {
        t.checkDivisorsG(
                Rule.pred("divisors_divisors", 2),
                true,
                divisors -> Compact.of(divisors)
                        .map(c -> {
                            final List<Integer> repeated = Collections.nCopies(c.n, c.p);

                            return IntStream.rangeClosed(0, c.n).boxed()
                                    .map(i -> repeated.subList(0, i))
                                    .collect(Collectors.toUnmodifiableSet());
                        })
                        .reduce(
                                Set.of(List.of()), (as, bs) -> as.stream()
                                        .flatMap(a -> bs.stream()
                                                .map(b -> Stream.concat(a.stream(), b.stream()).toList()))
                                        .collect(Collectors.toUnmodifiableSet()))
        );
    }


    // === 3534
    private static void cube(final PrimesTester t) {
        t.checkDivisors(Rule.pred("cube_divisors", 2), true, s -> s.flatMap(v -> IntStream.of(v, v, v)));
    }


    // === 3233
    private static void square(final PrimesTester t) {
        t.checkDivisors(Rule.pred("square_divisors", 2), true, s -> s.flatMap(v -> IntStream.of(v, v)));
    }


    // === Common code

    public static final Selector SELECTOR = new Selector(PrimesTest.class, "easy", "hard", "bonus")
            .variant("Primes", variant(t -> {}))
            .variant("3637", variant(PrimesTest::compact))
            .variant("3839", variant(PrimesTest::divisorsDivisors))
            .variant("3435", variant(PrimesTest::cube))
            .variant("3233", variant(PrimesTest::square))
            ;

    private PrimesTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */ static Consumer<TestCounter> variant(final Consumer<PrimesTester> check) {
        return counter -> {
            final int mode = counter.mode();
            final int max = (int) (1000 * Math.pow(100.0 / TestCounter.DENOMINATOR, mode));
            final int multiplier = (mode + 1) * 100 / TestCounter.DENOMINATOR2;
            new PrimesTester(counter, max, mode > 0, multiplier, check).test();
        };
    }
}
