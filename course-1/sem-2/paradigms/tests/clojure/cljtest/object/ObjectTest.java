package cljtest.object;

import base.Selector;

import static common.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-object-expressions">Clojure Object Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ObjectTest {
    public static final Selector SELECTOR = ObjectTester.builder()
            .variant("Base", NARY_ARITH)
            .variant("3637",       any(5, SUM_EXP, LME))
            .variant("3839",       any(5, SUM_EXP, SOFT_MAX))
            .variant("3233",       any(5, SUM_EXP, LSE))
            .variant("3435",       any(5, SUM_EXP, LME))
            .selector();

    private ObjectTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
