package expression.exceptions;

import expression.ListExpression;
import expression.Divide;

public class CheckedDivide extends Divide {
    public CheckedDivide(ListExpression first, ListExpression second) {
        super(first, second);
    }

    @Override
    protected int action(int first, int second) {
        if (second == 0) {
            throw new DivisionByZeroException(first, second);
        }

        if (first == Integer.MIN_VALUE && second == -1) {
            throw new OverflowException(getOperator(), first, second);
        }

        return first / second;
    }
}
