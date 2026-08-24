package common.expression;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record AnyOp(ExprTester.Func f, int min, int max, int fixed) {
    public Predicate<Collection<?>> arity() {
        return args -> min <= args.size() && args.size() <= max;
    }
}
