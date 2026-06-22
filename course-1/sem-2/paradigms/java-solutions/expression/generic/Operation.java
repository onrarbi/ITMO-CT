package expression.generic;

import java.util.List;

public interface Operation<T> {
    T add (T first, T second);
    T subtract (T first, T second);
    T multiply (T first, T second);
    T divide (T first, T second);
    T negate (T expression);

    List<T> values(int x, int y, int z);
    T valueOf(int value);
}
