package queue;

// Model: a[1]..a[n]
// Inv: n >= 0 && forall i=1..n: a[i] != null
// Let: immutable(k): forall i=1..k: a'[i] = a[i]
public interface Queue {
    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    void enqueue(Object element);

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable(n')
    Object dequeue();

    // Pre: n > 0
    // Post: R = a[1] && n' = n && immutable(n)
    Object element();

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    int size();

    // Pre: true
    // Post: n' = 0
    void clear();

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    boolean isEmpty();

    // Pre: value != null
    // Post: n' = |{i | 1 <= i <= n && a[i] != value}| && a' состоит из всех элементов a, не равных value, в исходном порядке
    void removeAll(Object value);

    // Pre: value != null
    // Post: n' = |{i | 1 <= i <= n && a[i] == value}| && a' состоит из всех элементов a, равных value, в исходном порядке
    void retainAll(Object value);
}
