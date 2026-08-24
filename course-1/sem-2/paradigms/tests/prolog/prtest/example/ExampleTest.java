package prtest.example;

import base.ExtendedRandom;
import base.Selector;
import base.TestCounter;
import prtest.PrologTest;
import prtest.Rule;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Tests for Example Prolog
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExampleTest extends PrologTest {
    private static final int RANGE = 1_000_000;
    private static final Rule ADD = Rule.func("add", 2);
    private static final Rule CONCAT = Rule.func("concat", 2);
    private static final Rule APPEND = Rule.func("append", 2);

    public ExampleTest(final TestCounter counter) {
        super(counter, Path.of("example.pl"));
    }

    @Override
    public void test() {
        counter.scope("Test add", () -> IntStream.range(0, 100).forEach(i -> counter.test(() -> {
            final int a = randomInt();
            final int b = randomInt();
            assertResult(a + b, ADD, a, b);
        })));
        testAppend("concat", CONCAT);
        testAppend("append", APPEND);
    }

    private void testAppend(final String name, final Rule rule) {
        counter.scope("Test " + name, () -> IntStream.range(0, 100).forEach(i -> counter.test(() -> {
            final List<String> list1 = randomStrings(i);
            final List<String> list2 = randomStrings(i);
            assertResult(Stream.of(list1, list2).flatMap(List::stream).toList(), rule, list1, list2);
        })));
    }

    private List<String> randomStrings(final int i) {
        return Stream.generate(() -> counter.random().randomString(ExtendedRandom.ENGLISH))
                .limit(counter.random().nextInt(i + 1))
                .toList();
    }

    private int randomInt() {
        return counter.random().nextInt(-RANGE, RANGE);
    }

    public static final Selector SELECTOR = new Selector(ExampleTest.class, "easy", "hard")
            .variant("base", counter -> new ExampleTest(counter).test());


    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
