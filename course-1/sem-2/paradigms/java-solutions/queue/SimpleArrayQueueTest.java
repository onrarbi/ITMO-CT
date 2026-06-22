package queue;

public class SimpleArrayQueueTest {

    private static void testEnqueueDequeue(ArrayQueue queue) {
        queue.clear();
        assert queue.isEmpty();
        int n = 5;

        for (int i = 0; i < n; i++) {
            queue.enqueue(i);
            assert queue.size() == i + 1;
            assert queue.element().equals(0);
            assert !queue.isEmpty();
        }

        for (int i = 0; i < n; i++) {
            assert queue.element().equals(i);
            Object removed = queue.dequeue();
            assert removed.equals(i);
            assert queue.size() == n - i - 1;
        }

        assert queue.size() == 0;
        assert queue.isEmpty();
    }

    private static void testClear(ArrayQueue queue) {
        queue.clear();
        for (int i = 0; i < 5; i++) {
            queue.enqueue(i);
        }

        assert queue.size() == 5;
        queue.clear();
        assert queue.size() == 0;
        assert queue.isEmpty();
    }

    private static void testEnsureCapacity(ArrayQueue queue) {
        queue.clear();
        int n = 20;

        for (int i = 0; i < n; i++) {
            queue.enqueue(i);
        }

        assert queue.size() == n;
        for (int i = 0; i < n; i++) {
            assert queue.element().equals(i);
            assert queue.dequeue().equals(i);
        }

        assert queue.isEmpty();
    }

    private static void testIndexOfLastIndexOf(ArrayQueue queue) {
        queue.clear();

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(2);

        assert queue.indexOf(2) == 1;
        assert queue.lastIndexOf(2) == 3;
        assert queue.indexOf(0) == -1;
        assert queue.size() == 4;
    }

    public static void main(String[] args) {
        System.out.println("Testing ArrayQueue");

        ArrayQueue q1 = new ArrayQueue();
        ArrayQueue q2 = new ArrayQueue();

        testEnqueueDequeue(q1);
        testClear(q1);
        testEnsureCapacity(q1);
        testIndexOfLastIndexOf(q1);
        testEnqueueDequeue(q2);
        testClear(q2);
        testEnsureCapacity(q2);
        testIndexOfLastIndexOf(q2);

        System.out.println("All tests passed!");
    }
}