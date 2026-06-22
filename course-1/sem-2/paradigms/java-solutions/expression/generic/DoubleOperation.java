package expression.generic;

import java.util.List;

public class DoubleOperation implements Operation<Double> {

    @Override
    public Double add(Double first, Double second) {
        return first + second;
    }

    @Override
    public Double subtract(Double first, Double second) {
        return first - second;
    }

    @Override
    public Double multiply(Double first, Double second) {
        return first * second;
    }

    @Override
    public Double divide(Double first, Double second) {
        return first / second;
    }

    @Override
    public Double negate(Double expression) {
        return -expression;
    }

    @Override
    public List<Double> values(int x, int y, int z) {
        return List.of((double)x, (double)y, (double)z);
    }

    @Override
    public Double valueOf(int value) {
        return (double)value;
    }
}
