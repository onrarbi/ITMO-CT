package expression.generic;

import java.util.List;
import java.util.Objects;

public class GenericConst<T> implements GenericExpression<T> {
    private final T value;

    public GenericConst(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public T evaluate(List<T> variables) {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericConst<?>)) {
            return false;
        }
        return value == ((GenericConst<?>) o).value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
