package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head = null;
    private Node tail = null;

    private static class Node {
        private final Object element;
        private Node next;
        public Node(Object elements, Node next) {
            assert elements != null;
            this.element = elements;
            this.next = next;
        }
    }

    @Override
    protected void enqueueImpl(Object element) {
        Node newTail = new Node(element, null);

        if (tail != null) {
            tail.next = newTail;
        } else {
            head = newTail;
        }
        tail = newTail;
    }

    @Override
    protected Object dequeueImpl() {
        Object result = head.element;
        head = head.next;

        if (head == null) {
            tail = null;
        }

        return result;
    }

    @Override
    protected Object elementImpl() {
        return head.element;
    }

    @Override
    protected void clearImpl() {
        head = null;
        tail = null;
    }
}
