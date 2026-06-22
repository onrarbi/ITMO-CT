package expression.generic;

import java.util.List;
import java.util.Objects;

public class GenericVariable<T> implements GenericExpression<T> {
    private final int id;

    public GenericVariable(int id) {
        this.id = id;
    }

    @Override
    public T evaluate(List<T> variables) {
        if (id >= 0 && id < variables.size()) {
            return variables.get(id);
        }
        throw new IllegalArgumentException("Wrong index: $" + id);
    }

    @Override
    public String toString() {
        return "$" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GenericVariable<?>)) {
            return false;
        }

        return id == ((GenericVariable<?>) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
