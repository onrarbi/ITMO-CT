package expression.generic;

import expression.exceptions.DivisionByZeroException;

import java.util.List;

public class UnsignedOperation implements Operation<Integer> {

    @Override
    public Integer add(Integer first, Integer second) {
        return first + second;
    }

    @Override
    public Integer subtract(Integer first, Integer second) {
        return first - second;
    }

    @Override
    public Integer multiply(Integer first, Integer second) {
        return first * second;
    }

    @Override
    public Integer divide(Integer first, Integer second) {
        if (second == 0) {
            throw new DivisionByZeroException(first, second);
        }
        return first / second;
    }

    @Override
    public Integer negate(Integer expression) {
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
