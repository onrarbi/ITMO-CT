package queue;

import java.util.Arrays;
import java.util.Objects;

public class ArrayQueue extends AbstractQueue {
    private static final int CAPACITY_SIZE = 2;
    private Object[] elements = new Object[CAPACITY_SIZE];
    private int head, tail;

    private void ensureCapacity(int capacity) {
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

    @Override
    protected void enqueueImpl(Object element) {
        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = (tail + 1) % elements.length;
    }

    @Override
    protected Object dequeueImpl() {
        Object result = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;

        return result;
    }

    @Override
    protected void clearImpl() {
        Arrays.fill(elements, null);
        head = tail = 0;
    }

    @Override
    protected Object elementImpl() {
        return elements[head];
    }

    public int indexOf(Object element) {
        Objects.requireNonNull(element);

        for (int i = 0; i < this.size; i++) {
            if (element.equals(this.elements[(this.head + i) % this.elements.length])) {
                return i;
            }
        }

        return -1;
    }

    public int lastIndexOf(Object element) {
        Objects.requireNonNull(element);

        for (int i = size - 1; i >= 0; i--) {
            if (element.equals(this.elements[(this.head + i) % this.elements.length])) {
                return i;
            }
        }

        return -1;
    }
}
