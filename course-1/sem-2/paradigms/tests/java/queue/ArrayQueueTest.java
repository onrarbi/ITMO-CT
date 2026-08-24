package queue;

import base.Selector;
import base.TestCounter;

import java.util.List;
import java.util.function.Consumer;

import static queue.Queues.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ArrayQueueTest {
    public static final Selector SELECTOR = new Selector(ArrayQueueTest.class)
            .variant("Base", variant(QueueModel.class, d -> () -> d))
            .variant("3637", variant(DequeCountModel.class, (DequeChecker<DequeCountModel>) d -> () -> d, DEQUE_COUNT))
            .variant("3839", variant(DequeCountIfModel.class, (DequeChecker<DequeCountIfModel>) d -> () -> d, DEQUE_COUNT_IF))
            .variant("3435", variant(IndexIfModel.class, d -> () -> d, INDEX_IF))
            .variant("3233", variant(IndexModel.class, d -> () -> d, INDEX))
            ;

    private ArrayQueueTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }

    /* package-private */
    static <M extends QueueModel> Consumer<TestCounter> variant(
            final Class<M> type,
            final QueueChecker<M> tester,
            final Splitter<M> splitter
    ) {
        return new ArrayQueueTester<>(type, tester, splitter)::test;
    }

    /* package-private */
    static <M extends QueueModel> Consumer<TestCounter> variant(
            final Class<M> type,
            final QueueChecker<M> tester
    ) {
        return variant(type, tester, (t, q, r) -> List.of());
    }
}
