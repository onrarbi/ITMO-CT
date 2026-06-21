package expression;

import java.util.List;
import java.util.Objects;

public class Const extends AbstractExpression {
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public int evaluate(List<Integer> variables) {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Const cnst)) {
            return false;
        }
        return value == cnst.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
