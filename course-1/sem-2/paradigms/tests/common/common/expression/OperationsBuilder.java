package common.expression;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Operations builder.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface OperationsBuilder {
    void constant(String name, String alias, double value);

    void variable(String name);

    void unary(String name, String alias, DoubleUnaryOperator op);

    void brackets(String left, String right, String alias, DoubleUnaryOperator op);

    void binary(String name, String alias, DoubleBinaryOperator op);

    void infix(String name, String alias, int priority, DoubleBinaryOperator op);

    void fixed(String name, String alias, int arity, ExprTester.Func f);

    void any(String name, String alias, AnyOp op);

    void alias(String name, String alias);

    void remove(String... names);

    BaseVariant variant();
}
