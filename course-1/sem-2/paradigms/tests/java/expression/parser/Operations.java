package expression.parser;

import expression.ToMiniString;
import expression.common.ExpressionKind;
import expression.common.Reason;

import java.util.function.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Operations {
    // === Base

    public static final Operation NEGATE = unary("-", 1, a -> -a);
    @SuppressWarnings("Convert2MethodRef")
    public static final Operation ADD       = binary("+", 1600, (a, b) -> a + b);
    public static final Operation SUBTRACT  = binary("-", 1602, (a, b) -> a - b);
    public static final Operation MULTIPLY  = binary("*", 2001, (a, b) -> a * b);
    public static final Operation DIVIDE    = binary("/", 2002, (a, b) -> b == 0 ? Reason.DBZ.error() : a / b);


    // === 3637
    public static final Operation ABS = unary("‖", "‖", Math::abs);
    public static final Operation CUBE = unary("³", 2, (a, c) -> c.applyAsInt(a * a) * a);
    public static final Operation CBRT = unary("∛", 1, a -> (long) Math.cbrt(a));

    // === 3839
    private static final Reason NEGATIVE_SQRT = new Reason("Square root of negative value");
    public static final Operation SQUARE = unary("²", 2, a -> a * a);
    public static final Operation SQRT = unary("√", 1, NEGATIVE_SQRT.less(0, a -> (long) Math.sqrt(a)));


    // === Common

    private Operations() {
    }

    public static Operation unary(final String name, final int priority, final LongUnaryOperator op) {
        return unary(name, priority, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String left, final String right, final LongUnaryOperator op) {
        return unary(left, right, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String name, final int priority, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(name, priority, op);
    }

    public static Operation unary(final String left, final String right, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(left, right, op);
    }

    public static Operation binary(final String name, final int priority, final LongBinaryOperator op) {
        return tests -> tests.binary(name, priority, op);
    }

    public static <E extends ToMiniString, C> Operation kind(
            final ExpressionKind<E, C> kind,
            final ParserTestSet.Parser<E> parser
    ) {
        return factory -> factory.kind(kind, parser);
    }

    @FunctionalInterface
    public interface Operation extends Consumer<ParserTester> {}
}
