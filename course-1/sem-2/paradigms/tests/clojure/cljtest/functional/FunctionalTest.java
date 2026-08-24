package cljtest.functional;

import base.Selector;

import static common.expression.Operations.*;

/**
 * Tests for
 * <a href="https://www.kgeorgiy.info/courses/paradigms/homeworks.html#clojure-functional-expressions">Clojure Functional Expressions</a>
 * homework of <a href="https://www.kgeorgiy.info/courses/paradigms">Programming Paradigms</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class FunctionalTest {
    public static final Selector SELECTOR = FunctionalTester.builder()
            .variant("Base", NARY_ARITH)
            .variant("3637", any(5, SUM_EXP, SOFT_MAX))
            .variant("3839", any(5, GEOM_MEAN, HARM_MEAN))
            .variant("3435", any(5, ARITH_MEAN, GEOM_MEAN))
            .variant("3233", ASIN, ACOS, any(2, ATAN_12))
            .selector();

    private FunctionalTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
