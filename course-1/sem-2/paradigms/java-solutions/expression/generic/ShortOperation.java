package expression.generic;

import expression.exceptions.DivisionByZeroException;

import java.util.List;

public class ShortOperation implements Operation<Short>{
    @Override
    public Short add(Short first, Short second) {
        return (short)(first + second);
    }

    @Override
    public Short subtract(Short first, Short second) {
        return (short)(first - second);
    }

    @Override
    public Short multiply(Short first, Short second) {
        return (short)( first * second);
    }

    @Override
    public Short divide(Short first, Short second) {
        if (second == 0) {
            throw new DivisionByZeroException(first, second);
        }
        return (short) (first / second);
    }

    @Override
    public Short negate(Short expression) {
        return (short)-expression;
    }

    @Override
    public List<Short> values(int x, int y, int z) {
        return List.of((short)x, (short)y, (short)z);
    }

    @Override
    public Short valueOf(int value) {
        return (short)value;
    }
}