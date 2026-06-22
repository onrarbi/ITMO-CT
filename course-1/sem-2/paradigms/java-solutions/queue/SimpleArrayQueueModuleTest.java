package queue;

public class SimpleArrayQueueModuleTest {

    private static void testEnqueueDequeue() {
        ArrayQueueModule.clear();
        assert ArrayQueueModule.isEmpty();
        int n = 5;

        for (int i = 0; i < n; i++) {
            ArrayQueueModule.enqueue(i);
            assert ArrayQueueModule.size() == i + 1;
            assert ArrayQueueModule.element().equals(0);
            assert !ArrayQueueModule.isEmpty();
        }

        for (int i = 0; i < n; i++) {
            assert ArrayQueueModule.element().equals(i);
            Object removed = ArrayQueueModule.dequeue();
            assert removed.equals(i);
            assert ArrayQueueModule.size() == n - i - 1;
        }

        assert ArrayQueueModule.size() == 0;
        assert ArrayQueueModule.isEmpty();
    }

    private static void testClear() {
        ArrayQueueModule.clear();

        for (int i = 0; i < 5; i++) {
            ArrayQueueModule.enqueue(i);
        }

        assert ArrayQueueModule.size() == 5;
        ArrayQueueModule.clear();
        assert ArrayQueueModule.size() == 0;
        assert ArrayQueueModule.isEmpty();
    }

    private static void testEnsureCapacity() {
        ArrayQueueModule.clear();
        int n = 20;

        for (int i = 0; i < n; i++) {
            ArrayQueueModule.enqueue(i);
        }

        assert ArrayQueueModule.size() == n;
        for (int i = 0; i < n; i++) {
            assert ArrayQueueModule.element().equals(i);
            assert ArrayQueueModule.dequeue().equals(i);
        }

        assert ArrayQueueModule.isEmpty();
    }

    private static void testIndexOfLastIndexOf() {
        ArrayQueueModule.clear();

        ArrayQueueModule.enqueue(1);
        ArrayQueueModule.enqueue(2);
        ArrayQueueModule.enqueue(3);
        ArrayQueueModule.enqueue(2);

        assert ArrayQueueModule.indexOf(2) == 1;
        assert ArrayQueueModule.lastIndexOf(2) == 3;
        assert ArrayQueueModule.indexOf(0) == -1;
        assert ArrayQueueModule.size() == 4;
    }

    public static void main(String[] args) {
        System.out.println("Testing ArrayQueueModule");

        testEnqueueDequeue();
        testClear();
        testEnsureCapacity();
        testIndexOfLastIndexOf();

        System.out.println("All tests passed!");
    }
}