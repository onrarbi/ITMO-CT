package queue;

public abstract class AbstractQueue implements Queue {
    protected int size;

    @Override
    public void enqueue(Object element) {
        assert element != null;
        enqueueImpl(element);
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    @Override
    public Object element() {
        assert size > 0;

        return elementImpl();
    }

    protected abstract Object elementImpl();

    @Override
    public Object dequeue() {
        assert size > 0;
        Object result = dequeueImpl();
        size--;

        return result;
    }

    protected abstract Object dequeueImpl();

    @Override
    public void clear() {
        clearImpl();
        size = 0;
    }

    protected abstract void clearImpl();

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void removeAll(Object value) {
        int currSize = size;

        for (int i = 0; i < currSize; i++) {
            Object element = dequeue();

            if (!element.equals(value)) {
                enqueue(element);
            }
        }
    }

    @Override
    public void retainAll(Object value) {
        int currSize = size;

        for (int i = 0; i < currSize; i++) {
            Object element = dequeue();

            if (element.equals(value)) {
                enqueue(element);
            }
        }
    }
}
