package queue;

public class SimpleArrayQueueADTTest {

    private static void testEnqueueDequeue(ArrayQueueADT queue) {
        ArrayQueueADT.clear(queue);
        assert ArrayQueueADT.isEmpty(queue);
        int n = 5;

        for (int i = 0; i < n; i++) {
            ArrayQueueADT.enqueue(queue, i);
            assert ArrayQueueADT.size(queue) == i + 1;
            assert ArrayQueueADT.element(queue).equals(0);
            assert !ArrayQueueADT.isEmpty(queue);
        }

        for (int i = 0; i < n; i++) {
            assert ArrayQueueADT.element(queue).equals(i);
            Object removed = ArrayQueueADT.dequeue(queue);
            assert removed.equals(i);
            assert ArrayQueueADT.size(queue) == n - i - 1;
        }

        assert ArrayQueueADT.size(queue) == 0;
        assert ArrayQueueADT.isEmpty(queue);
    }

    private static void testClear(ArrayQueueADT queue) {
        ArrayQueueADT.clear(queue);

        for (int i = 0; i < 5; i++) {
            ArrayQueueADT.enqueue(queue, i);
        }

        assert ArrayQueueADT.size(queue) == 5;
        ArrayQueueADT.clear(queue);
        assert ArrayQueueADT.size(queue) == 0;
        assert ArrayQueueADT.isEmpty(queue);
    }

    private static void testEnsureCapacity(ArrayQueueADT queue) {
        ArrayQueueADT.clear(queue);
        int n = 20;

        for (int i = 0; i < n; i++) {
            ArrayQueueADT.enqueue(queue, i);
        }

        assert ArrayQueueADT.size(queue) == n;
        for (int i = 0; i < n; i++) {
            assert ArrayQueueADT.element(queue).equals(i);
            assert ArrayQueueADT.dequeue(queue).equals(i);
        }

        assert ArrayQueueADT.isEmpty(queue);
    }

    private static void testIndexOfLastIndexOf(ArrayQueueADT queue) {
        ArrayQueueADT.clear(queue);

        ArrayQueueADT.enqueue(queue, 1);
        ArrayQueueADT.enqueue(queue, 2);
        ArrayQueueADT.enqueue(queue, 3);
        ArrayQueueADT.enqueue(queue, 2);

        assert ArrayQueueADT.indexOf(queue, 2) == 1;
        assert ArrayQueueADT.lastIndexOf(queue, 2) == 3;
        assert ArrayQueueADT.indexOf(queue, 0) == -1;
        assert ArrayQueueADT.size(queue) == 4;
    }

    public static void main(String[] args) {
        System.out.println("Testing ArrayQueueADT");

        ArrayQueueADT q1 = new ArrayQueueADT();
        ArrayQueueADT q2 = new ArrayQueueADT();

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