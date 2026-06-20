package expression.exceptions;

import expression.Add;
import expression.ListExpression;

public class CheckedAdd extends Add {
    public CheckedAdd(ListExpression first, ListExpression second) {
        super(first, second);
    }

    @Override
    protected int action(int first, int second) {
        if (second > 0 && first > Integer.MAX_VALUE - second) {
            throw new OverflowException(getOperator(), first, second);
        }

        if (second < 0 && first < Integer.MIN_VALUE - second) {
            throw new OverflowException(getOperator(), first, second);
        }

        return first + second;
    }
}
