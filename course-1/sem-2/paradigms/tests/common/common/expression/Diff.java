package common.expression;

import base.Asserts;
import base.Named;
import common.Engine;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static common.expression.ExprTester.EPS;
import static common.expression.ExprTester.Test;

/**
 * Expression differentiator.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Diff {
    private static final double D = 1e-6;

    private final int base;
    private final Dialect dialect;

    public Diff(final int base, final Dialect dialect) {
        this.dialect = dialect;
        this.base = base;
    }

    public <X> void diff(final ExprTester<X> tester, final boolean reparse) {
        tester.addStage(() -> {
            for (final Test expr : tester.language.getTests()) {
                checkDiff(tester, expr, reparse, false);
            }
        });
    }

    private <X> List<Engine.Result<String>> checkDiff(
            final ExprTester<X> tester,
            final Test test,
            final boolean reparse,
            final boolean simplify
    ) {
        final List<Engine.Result<String>> results = new ArrayList<>(test.variables().size() + 1);
        System.out.println("    Testing diff: " + test.parsed());

        if (simplify) {
            final Engine.Result<X> simplified = tester.engine.prepare(dialect.meta("simplify", test.parsed()));
            test.points().forEachOrdered(point -> {
                final double[] vars = Arrays.stream(point).map(v -> v + base).toArray();
                tester.assertValue("simplified expression", simplified, vars, test.evaluate(vars));
            });
            results.add(tester.engine.toString(simplified));
        }

        final double[] indices = IntStream.range(0, test.variables().size()).mapToDouble(a -> a).toArray();
        for (final Expr variable : test.variables()) {
            final List<Named<Engine.Result<X>>> ways = new ArrayList<>();
            final String diffS = dialect.meta("diff", test.parsed(), dialect.render(variable));
            addWays("diff", tester, reparse, diffS, ways);

            if (simplify) {
                final String simplifyS = dialect.meta("simplify", diffS);
                results.add(tester.engine.toString(addWays("simplified", tester, reparse, simplifyS, ways)));
            }

            final int index = (int) variable.evaluate(indices);

            test.points().forEachOrdered(point -> {
                final double[] vars = Arrays.stream(point).map(v -> v + base).toArray();
                final double center = test.evaluate(vars);
                if (ok(center)) {
                    final double lft = evaluate(test, vars, index, -D);
                    final double rt = evaluate(test, vars, index, D);
                    final double left = (center - lft) / D;
                    final double right = (rt - center) / D;
                    if (ok(lft) && ok(rt) && ok(left) && ok(right) && Math.abs(left - right) < EPS) {
                        for (final Named<Engine.Result<X>> way : ways) {
                            tester.assertValue(
                                    "diff by %s, %s".formatted(dialect.render(variable), way.name()),
                                    way.value(), vars, (left + right) / 2
                            );
                        }
                    }
                }
            });
        }
        return results;
    }

    private static <X> Engine.Result<X> addWays(
            final String name,
            final ExprTester<X> tester,
            final boolean reparse,
            final String exprS,
            final List<Named<Engine.Result<X>>> ways
    ) {
        final Engine.Result<X> exprR = tester.engine.prepare(exprS);
        ways.add(Named.of(name, exprR));
        if (reparse) {
            ways.add(Named.of("reparsed " + name, tester.parse(tester.engine.toString(exprR).value())));
        }
        return exprR;
    }

    private static boolean ok(final double value) {
        final double abs = Math.abs(value);
        return EPS < abs && abs < 1 / EPS;
    }

    private static double evaluate(final Test test, final double[] vars, final int index, final double d) {
        vars[index] += d;
        final double result = test.evaluate(vars);
        vars[index] -= d;
        return result;
    }

    public <X> void simplify(final ExprTester<X> tester) {
        final List<int[]> simplifications = tester.language.getSimplifications();
        if (simplifications == null) {
            return;
        }

        tester.addStage(() -> {
            final List<int[]> newSimplifications = new ArrayList<>();
            final List<Test> tests = tester.language.getTests();

            for (int i = 0; i < simplifications.size(); i++) {
                final Test expr = tests.get(i);
                final int[] expected = simplifications.get(i);
                final List<Engine.Result<String>> actual = checkDiff(tester, expr, true, true);
                if (expected != null) {
                    for (int j = 0; j < expected.length; j++) {
                        final Engine.Result<String> result = actual.get(j);
                        final int length = result.value().length();
                        Asserts.assertTrue(
                                "Simplified length too long: %d instead of %d%s"
                                        .formatted(length, expected[j], result.context()),
                                length <= expected[j]
                        );
                    }
                } else {
                    newSimplifications.add(actual.stream().mapToInt(result -> result.value().length()).toArray());
                }
            }
            if (!newSimplifications.isEmpty()) {
                System.err.println(newSimplifications.stream()
                        .map(row -> Arrays.stream(row)
                                .mapToObj(Integer::toString)
                                .collect(Collectors.joining(", ", "{", "}")))
                        .collect(Collectors.joining(", ", "new int[][]{", "}")));
                System.err.println(simplifications.size() + " " + newSimplifications.size());
                throw new AssertionError("Uncovered");
            }
        });
    }
}
