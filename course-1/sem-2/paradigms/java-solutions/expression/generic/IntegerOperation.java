package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

import java.util.List;

public class IntegerOperation implements Operation<Integer> {

    @Override
    public Integer add(Integer first, Integer second) {
        if (first > 0 && second > 0 && first > Integer.MAX_VALUE - second) {
            throw new OverflowException("+", first, second);
        }

        if (first < 0 && second < 0 && first < Integer.MIN_VALUE - second) {
            throw new OverflowException("+", first, second);
        }

        return first + second;
    }

    @Override
    public Integer subtract(Integer first, Integer second) {
        if (second > 0 && first < Integer.MIN_VALUE + second) {
            throw new OverflowException("-", first, second); }

        if (second < 0 && first > Integer.MAX_VALUE + second) {
            throw new OverflowException("-", first, second);
        }

        return first - second;
    }

    @Override
    public Integer multiply(Integer first, Integer second) {
        if (first > 0) {
            if (second > 0) {
                if (first > Integer.MAX_VALUE / second) {
                    throw new OverflowException("*", first, second);
                }
            } else if (second < 0) {
                if (second < Integer.MIN_VALUE / first) {
                    throw new OverflowException("*", first, second);
                }
            }
        } else if (first < 0) {
            if (second > 0) {
                if (first < Integer.MIN_VALUE / second) {
                    throw new OverflowException("*", first, second);
                }
            } else if (second < 0) {
                if (first < Integer.MAX_VALUE / second) {
                    throw new OverflowException("*", first, second);
                }
            }
        }

        return first * second;
    }

    @Override
    public Integer divide(Integer first, Integer second) {
        if (second == 0) {
            throw new DivisionByZeroException(first, second);
        }
        if (first == Integer.MIN_VALUE && second == -1) {
            throw new OverflowException("/", first, second);
        }

        return first / second;
    }

    @Override
    public Integer negate(Integer expression) {
        if (expression == Integer.MIN_VALUE) {
            throw new OverflowException("-", expression);
        }

        return -expression;
    }

    @Override
    public List<Integer> values(int x, int y, int z) {
        return List.of(x, y, z);
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }
}
