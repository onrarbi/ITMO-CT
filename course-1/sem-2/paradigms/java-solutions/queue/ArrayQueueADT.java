package queue;

import java.util.Arrays;
import java.util.Objects;

// Model: a[1]..a[n] && a[i] != null
// Inv: n >= 0 && n <= elements.length && head >= 0 && head < elements.length && tail >= 0 && tail < elements.length &&
// && tail == (head + size) % elements.length
// Let: immutable(k): forall i=1..k: a'[i] = a[i]
public class ArrayQueueADT {
    private static final int CAPACITY_SIZE = 10;
    private int head;
    private int tail;
    private int size;
    private Object[] elements;

    public ArrayQueueADT() {
        this.elements = new Object[CAPACITY_SIZE];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    // Pre: capacity >= 0 && queue != null
    // Post: n' = n && immutable(n)
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] newElements = new Object[queue.elements.length * 2];

            if (queue.head < queue.tail) {
                System.arraycopy(queue.elements, queue.head, newElements, 0, queue.size);
            } else {
                System.arraycopy(queue.elements, queue.head, newElements, 0, queue.elements.length - queue.head);
                System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.head, queue.tail);
            }

            queue.elements = newElements;
            queue.head = 0;
            queue.tail = queue.size;
        }
    }

    // Pre: queue != null && element != null
    // Post: n' = n + 1 &&
    //       a'[n'] = element &&
    //       immutable(n)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(queue, queue.size + 1);
        queue.elements[queue.tail] = element;
        queue.tail = (queue.tail + 1) % queue.elements.length;
        queue.size++;
    }

    // Pre: queue != null && n > 0
    // Post: R = a[1] && n' = n - 1 && immutable(n')
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.size--;

        return result;
    }

    // Pre: queue != null && n > 0
    // Post: R = a[1] && n' = n && immutable(n)
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;

        return queue.elements[queue.head];
    }

    // Pre: queue != null
    // Post: R = n && n' = n && immutable(n)
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    // Pre: queue != null
    // Post: R = (n = 0) && n' = n && immutable(n)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    // Pre: true
    // Post: n' = 0
    public static void clear(ArrayQueueADT queue) {
        Arrays.fill(queue.elements, null);
        queue.head = queue.tail = queue.size = 0;
    }

    // Pre: element != null & queue != null
    // Post: R = min { i | 0 ≤ i < n && a[i] = element } || R = -1 &&
    //       n' = n && immutable(n)
    public static int indexOf(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);

        for (int i = 0; i < queue.size; i++) {
            if (element.equals(queue.elements[(queue.head + i) % queue.elements.length])) {
                return i;
            }
        }

        return -1;
    }

    // Pre: element != null & queue != null
    // Post: R = max { i | 0 ≤ i < n && a[i] = element } || R = -1 &&
    //       n' = n && immutable(n)
    public static int lastIndexOf(ArrayQueueADT queue, Object element) {
        Objects.requireNonNull(element);

        for (int i = queue.size - 1; i >= 0; i--) {
            if (element.equals(queue.elements[(queue.head + i) % queue.elements.length])) {
                return i;
            }
        }

        return -1;
    }
}
