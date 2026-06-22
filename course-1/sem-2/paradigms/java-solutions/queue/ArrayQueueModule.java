package queue;

import java.util.Arrays;
import java.util.Objects;

// Model: a[1]..a[n] && a[i] != null
// Inv: n >= 0 && n <= elements.length && head >= 0 && head < elements.length && tail >= 0 && tail < elements.length &&
// && tail == (head + size) % elements.length
// Let: immutable(k): forall i=1..k: a'[i] = a[i]
public class ArrayQueueModule {
    private static final int CAPACITY_SIZE = 5;
    private static int head;
    private static int tail;
    private static int size;
    private static Object[] elements = new Object[CAPACITY_SIZE];

    // Pre: capacity >= 0
    // Post: n' = n && immutable(n)
    private static void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            if (head < tail) {
                System.arraycopy(elements, head, newElements, 0, size);
            } else {
                System.arraycopy(elements, head, newElements, 0, elements.length - head);
                System.arraycopy(elements, 0, newElements, elements.length - head, tail);
            }
            elements = newElements;
            head = 0;
            tail = size;
        }
    }

    // Pre: element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
        size++;
    }

    // Pre: n > 0
    // Post: R = a[1] && n' = n - 1 && immutable(n')
    public static Object dequeue() {
        assert size > 0;
        Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return result;
    }

    // Pre: n > 0
    // Post: R = a[1] && n' = n && immutable(n)
    public static Object element() {
        assert !isEmpty();
        return elements[head];
    }

    // Pre: true
    // Post: R = n && n' = n && immutable(n)
    public static int size() {
        return size;
    }

    // Pre: true
    // Post: R = (n = 0) && n' = n && immutable(n)
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pre: true
    // Post: n' = 0
    public static void clear() {
        Arrays.fill(elements, null);
        head = tail = size = 0;
    }

    // Pre: element != null
    // Post: R = min { i | 0 ≤ i < n && a[i] = element } || R = -1 &&
    //       n' = n && immutable(n)
    public static int indexOf(Object element) {
        Objects.requireNonNull(element);
        for (int i = 0; i < size; i++) {
            if (element.equals(elements[(head + i) % elements.length])) {
                return i;
            }
        }
        return -1;
    }

    // Pre: element != null
    // Post: R = max { i | 0 ≤ i < n && a[i] = element } || R = -1 &&
    //       n' = n && immutable(n)
    public static int lastIndexOf(Object element) {
        Objects.requireNonNull(element);
        for (int i = size - 1; i >= 0; i--) {
            if (element.equals(elements[(head + i) % elements.length])) {
                return i;
            }
        }
        return -1;
    }
}
