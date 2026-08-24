package cljtest.linear;

import base.ExtendedRandom;
import base.Selector;
import base.TestCounter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;


/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-linear">Linear Clojure</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class LinearTest {
    // === Shapeless

    private static final List<Item.Fun> SHAPELESS = Item.functions("s");

    private static Item genShapeless(final ExtendedRandom random, final int complexity) {
        if (complexity == 0) {
            return Item.ZERO;
        }
        final int[] parts = new int[1 + random.nextInt(Math.min(complexity, 5))];
        for (int i = parts.length; i < complexity; i++) {
            parts[random.nextInt(parts.length)]++;
        }
        return Item.vector(Arrays.stream(parts).mapToObj(c -> genShapeless(random, c)));
    }

    private static void shapeless(final Test test) {
        IntStream.range(0, 100 / TestCounter.DENOMINATOR2)
                .forEachOrdered(complexity -> test.test(() -> genShapeless(test.random(), complexity)));
    }


    // === Selector

    public static final Selector SELECTOR = new Selector(LinearTester.class, "easy", "hard")
            .variant("Base", v(LinearTester::new))
            .variant("3637", v(SimplexTester::new))
            .variant("3839", v(BroadcastTester::new))
            .variant("3435", variant(SHAPELESS, LinearTest::shapeless))
            .variant("3233", variant(SHAPELESS, LinearTest::shapeless))
            ;

    private LinearTest() {
    }

    /* package-private*/ static Consumer<TestCounter> v(final Function<TestCounter, LinearTester> variant) {
        return counter -> variant.apply(counter).test();
    }

    /* package-private*/ static Consumer<TestCounter> variant(final List<Item.Fun> functions, final Consumer<Test> variant) {
        return v(counter -> new LinearTester(counter) {
            @Override
            protected void test(final int args) {
                variant.accept(new Test(this, functions, args));
            }
        });
    }

    /* package-private */ record Test(
            LinearTester test,
            List<Item.Fun> functions,
            int args
    ) {
        public void test(final Supplier<Item> generator) {
            test.test(args, functions, Item.same(generator));
        }

        public void test(final IntFunction<List<Item>> generator) {
            test.test(args, functions, generator);
        }

        public boolean isHard() {
            return test.isHard();
        }

        public void expectException(final int[] okDims, final int[][] failDims) {
            test.expectException(functions, okDims, failDims);
        }

        public ExtendedRandom random() {
            return test.random();
        }
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
