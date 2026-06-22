package expression.generic;

import java.util.List;

public interface GenericExpression<T> {
    T evaluate(List<T> variables);

    @Override
    String toString();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}
