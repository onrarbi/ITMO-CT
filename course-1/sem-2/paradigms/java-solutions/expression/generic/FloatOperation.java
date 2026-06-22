package expression.generic;

import java.util.List;

public class FloatOperation implements Operation<Float> {

    @Override
    public Float add(Float first, Float second) {
        return first + second;
    }

    @Override
    public Float subtract(Float first, Float second) {
        return first - second;
    }

    @Override
    public Float multiply(Float first, Float second) {
        return first * second;
    }

    @Override
    public Float divide(Float first, Float second) {
        return first / second;
    }

    @Override
    public Float negate(Float expression) {
        return -expression;
    }

    @Override
    public List<Float> values(int x, int y, int z) {
        return List.of((float)x, (float)y, (float)z);
    }

    @Override
    public Float valueOf(int value) {
        return (float) value;
    }
}
