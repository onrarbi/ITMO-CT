package expression.exceptions;

import expression.ListExpression;
import expression.Multiply;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(ListExpression first, ListExpression second) {
        super(first, second);
    }

    @Override
    protected int action(int first, int second) {
        if (first > 0) {
            if (second > 0 && first > Integer.MAX_VALUE / second) {
                throw new OverflowException(getOperator(), first, second);
            }

            if (second < 0 && second < Integer.MIN_VALUE / first) {
                throw new OverflowException(getOperator(), first, second);
            }
        } else if (first < 0) {
            if (second > 0 && first < Integer.MIN_VALUE / second) {
                throw new OverflowException(getOperator(), first, second);
            }

            if (second < 0 && first < Integer.MAX_VALUE / second) {
                throw new OverflowException(getOperator(), first, second);
            }
        }

        return first * second;
    }
}
