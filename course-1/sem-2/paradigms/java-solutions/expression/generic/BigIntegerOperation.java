package expression.generic;

import expression.exceptions.DivisionByZeroException;

import java.math.BigInteger;
import java.util.List;

public class BigIntegerOperation implements Operation<BigInteger> {

    @Override
    public BigInteger add(BigInteger first, BigInteger second) {
        return first.add(second);
    }

    @Override
    public BigInteger subtract(BigInteger first, BigInteger second) {
        return first.subtract(second);
    }

    @Override
    public BigInteger multiply(BigInteger first, BigInteger second) {
        return first.multiply(second);
    }

    @Override
    public BigInteger divide(BigInteger first, BigInteger second) {
        if (second.equals(BigInteger.ZERO)) throw new DivisionByZeroException(first.intValue(), second.intValue());
        return first.divide(second);
    }

    @Override
    public BigInteger negate(BigInteger expression) {
        return expression.negate();
    }

    @Override
    public List<BigInteger> values(int x, int y, int z) {
        return List.of(BigInteger.valueOf(x), BigInteger.valueOf(y), BigInteger.valueOf(z));
    }

    @Override
    public BigInteger valueOf(int value) {
        return BigInteger.valueOf(value);
    }
}
