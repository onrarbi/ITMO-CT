package expression.exceptions;

import base.Selector;
import expression.ListExpression;
import expression.parser.Operations;

import static expression.parser.Operations.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ExceptionsTest {
    private static final ExpressionParser PARSER = new ExpressionParser();
    private static final Operations.Operation LIST = kind(ListExpression.KIND, PARSER::parse);

    public static final Selector SELECTOR = Selector.composite(ExceptionsTest.class, ExceptionsTester::new, "easy", "hard")
            .variant("Base", LIST, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("3637", ABS, CUBE, CBRT)
            .variant("3839", ABS, CUBE, CBRT, SQUARE, SQRT)
            .variant("3435", ABS, SQRT)
            .variant("3233", ABS, CBRT)
            .selector();

    private ExceptionsTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}
