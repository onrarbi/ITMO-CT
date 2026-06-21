package expression.exceptions;

import expression.ListExpression;
import expression.Module;

public class CheckedModule extends Module {
    public CheckedModule(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int expression) {
        if (expression == Integer.MIN_VALUE) {
            throw new OverflowException(getOperator(), expression);
        }

        return expression < 0 ? -expression : expression;
    }
}