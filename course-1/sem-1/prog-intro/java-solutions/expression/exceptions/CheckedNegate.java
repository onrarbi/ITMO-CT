package expression.exceptions;

import expression.ListExpression;
import expression.Negate;

public class CheckedNegate extends Negate {
    public CheckedNegate(ListExpression expression) {
        super(expression);
    }

    @Override
    protected int action(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException(getOperator(), value);
        }

        return -value;
    }
}
