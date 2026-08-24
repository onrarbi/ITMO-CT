package expression;

import base.ExtendedRandom;
import base.Pair;
import expression.common.ExpressionKind;
import expression.common.Type;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface ListExpression extends ToMiniString {
    ExpressionKind<ListExpression, Integer> KIND = new ExpressionKind<>(
            new Type<>(a -> a, ExtendedRandom::nextInt, int.class),
            ListExpression.class,
            (r, c) -> IntStream.range(0, c)
                    .mapToObj(name -> Pair.<String, ListExpression>of("$" + name, new Variable(name)))
                    .toList(),
            (expr, variables, values) -> expr.evaluate(values)
    );

    int evaluate(List<Integer> variables);
}
