package queue;

import base.ExtendedRandom;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("UnusedReturnValue")
public final class Queues {
    private Queues() {
    }

    protected interface QueueModel {
        @ReflectionTest.Ignore
        ArrayDeque<Object> model();

        default Object dequeue() {
            return model().removeFirst();
        }

        default int size() {
            return model().size();
        }

        default boolean isEmpty() {
            return model().isEmpty();
        }

        default void clear() {
            model().clear();
        }

        default void enqueue(final Object element) {
            model().addLast(element);
        }

        default Object element() {
            return model().getFirst();
        }
    }

    protected interface QueueChecker<M extends QueueModel> {
        M wrap(ArrayDeque<Object> reference);

        default List<M> linearTest(final M queue, final ExtendedRandom random) {
            // Do nothing by default
            return List.of();
        }

        default void check(final M queue, final ExtendedRandom random) {
            queue.element();
        }

        default void add(final M queue, final Object element, final ExtendedRandom random) {
            queue.enqueue(element);
        }

        default Object randomElement(final ExtendedRandom random) {
            return ArrayQueueTester.ELEMENTS[random.nextInt(ArrayQueueTester.ELEMENTS.length)];
        }

        default void remove(final M queue, final ExtendedRandom random) {
            queue.dequeue();
        }

        @SuppressWarnings("unchecked")
        default M cast(final QueueModel model) {
            return (M) model;
        }
    }

    @FunctionalInterface
    protected interface Splitter<M extends QueueModel> {
        List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);
    }

    @FunctionalInterface
    /* package-private */ interface LinearTester<M extends QueueModel> extends Splitter<M> {
        void test(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random);

        @Override
        default List<M> split(final QueueChecker<? extends M> tester, final M queue, final ExtendedRandom random) {
            test(tester, queue, random);
            return List.of();
        }
    }


    // === 3637 (ArrayQueue)

    /* package-private */ interface CountModel extends ReflectionModel {
        default int count(final Object element) {
            return reduce(0, element, (v, i) -> v + 1);
        }
    }

    /* package-private */ static final Queues.LinearTester<CountModel> COUNT =
            (tester, queue, random) -> queue.count(tester.randomElement(random));

    /* package-private */ interface DequeCountModel extends DequeModel, CountModel {
    }

    /* package-private */ static final Queues.LinearTester<DequeCountModel> DEQUE_COUNT = COUNT::test;


    // === 3839 (ArrayQueue)

    /* package-private */ interface CountIfModel extends ReflectionModel {
        default int countIf(final Predicate<Object> p) {
            return reduce(0, p, (v, i) -> v + 1);
        }
    }

    /* package-private */ static final Queues.LinearTester<CountIfModel> COUNT_IF =
            (t, q, r) -> q.countIf(randomPredicate(t, r));

    /* package-private */ interface DequeCountIfModel extends DequeModel, CountIfModel {
    }

    /* package-private */ static final Queues.LinearTester<DequeCountIfModel> DEQUE_COUNT_IF = COUNT_IF::test;

    record NamedPredicate<T> (String name, Predicate<? super T> predicate) implements Predicate<T> {
        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean test(final T t) {
            return predicate.test(t);
        }
    }

    /* package-private */ static Predicate<Object> randomPredicate(final Queues.QueueChecker<? extends Queues.QueueModel> tester, final ExtendedRandom random) {
        final Object item = tester.randomElement(random);
        return random.<Supplier<Predicate<Object>>>randomItem(
                () -> new NamedPredicate<>("item == ", o -> item == o),
                () -> new NamedPredicate<>(item + ".equals", item::equals),
                () -> new NamedPredicate<>("null == ", Objects::isNull)
        ).get();
    }

    @SafeVarargs
    private static <M extends Queues.QueueModel> Queues.LinearTester<M> randomOf(final Queues.LinearTester<? super M>... variants) {
        return (tester, queue, random)
                -> random.randomItem(variants).test(tester, queue, random);
    }

    // === 3435 (ArrayQueue)

    /* package-private */
    interface IndexIfModel extends ReflectionModel {
        default int indexIf(final Predicate<Object> p) {
            return reduce(-1, p, (v, i) -> v == -1 ? i : v);
        }

        default int lastIndexIf(final Predicate<Object> p) {
            return reduce(-1, p, (v, i) -> i);
        }
    }

    /* package-private */ static final Queues.LinearTester<IndexIfModel> INDEX_IF = randomOf(
            (t, q, r) -> q.indexIf(randomPredicate(t, r)),
            (t, q, r) -> q.lastIndexIf(randomPredicate(t, r))
    );


    // === 3233 (ArrayQueue)

    /* package-private */
    interface IndexModel extends ReflectionModel {
        default int indexOf(final Object element) {
            return reduce(-1, element, (v, i) -> v == -1 ? i : v);
        }

        default int lastIndexOf(final Object element) {
            return reduce(-1, element, (v, i) -> i);
        }
    }

    /* package-private */ static final Queues.LinearTester<IndexModel> INDEX = randomOf(
            (t, q, r) -> q.indexOf(t.randomElement(r)),
            (t, q, r) -> q.lastIndexOf(t.randomElement(r))
    );


    // === 3637 (Queues)

    /* package-private */ interface ContainsModel extends Queues.QueueModel {
        default boolean contains(final Object element) {
            return model().contains(element);
        }

        @SuppressWarnings("UnusedReturnValue")
        default boolean removeFirst(final Object element) {
            return model().removeFirstOccurrence(element);
        }
    }

    /* package-private */ static final Queues.LinearTester<ContainsModel> CONTAINS = (tester, queue, random) -> {
        final Object element = random.nextBoolean() ? tester.randomElement(random) : random.nextInt();
        if (random.nextBoolean()) {
            queue.contains(element);
        } else {
            queue.removeFirst(element);
        }
    };


    // === 3839 (Queues)

    /* package-private */ interface NthModel extends Queues.QueueModel {
        // Deliberately ugly implementation
        @ReflectionTest.Wrap
        default NthModel getNth(final int n) {
            final ArrayDeque<Object> deque = new ArrayDeque<>();
            final int[] index = {0};
            model().forEach(e -> {
                if (++index[0] % n == 0) {
                    deque.add(e);
                }
            });
            return () -> deque;
        }

        // Deliberately ugly implementation
        @ReflectionTest.Wrap
        default NthModel removeNth(final int n) {
            final ArrayDeque<Object> deque = new ArrayDeque<>();
            final int[] index = {0};
            model().removeIf(e -> {
                if (++index[0] % n == 0) {
                    deque.add(e);
                    return true;
                } else {
                    return false;
                }
            });
            return () -> deque;
        }

        default void dropNth(final int n) {
            final int[] index = {0};
            model().removeIf(e -> ++index[0] % n == 0);
        }
    }

    /* package-private */ static final Queues.Splitter<NthModel> NTH = (tester, queue, random) -> {
        final int n = random.nextInt(5) + 1;
        return switch (random.nextInt(3)) {
            case 0 -> {
                final NthModel model = queue.removeNth(n);
                yield List.of(tester.cast(model));
            }
            case 1 -> {
                queue.dropNth(n);
                yield List.of();
            }
            case 2 -> List.of(tester.cast(queue.getNth(n)));
            default -> throw new AssertionError();
        };
    };


    // === 3435 (Queues)

    /* package-private */ interface RemoveIfModel extends Queues.QueueModel {
        default void removeIf(final Predicate<Object> p) {
            model().removeIf(p);
        }

        default void retainIf(final Predicate<Object> p) {
            model().removeIf(Predicate.not(p));
        }
    }

    private static final Queues.LinearTester<RemoveIfModel> REMOVE_IF = (tester, queue, random) ->
            queue.removeIf(randomPredicate(tester, random));
    private static final Queues.LinearTester<RemoveIfModel> RETAIN_IF = (tester, queue, random) ->
            queue.retainIf(randomPredicate(tester, random));
    /* package-private */ static final Queues.LinearTester<RemoveIfModel> REMOVE_IF_TEST = randomOf(REMOVE_IF, RETAIN_IF);

    // 3233 (Queues)

    /* package-private */ interface RemoveEqModel extends Queues.QueueModel {
        default void removeAll(final Object element) {
            model().removeIf(Predicate.isEqual(element));
        }

        default void retainAll(final Object element) {
            model().removeIf(Predicate.not(Predicate.isEqual(element)));
        }
    }

    private static final Queues.LinearTester<RemoveEqModel> REMOVE_ALL = (tester, queue, random) ->
            queue.removeAll(tester.randomElement(random));
    private static final Queues.LinearTester<RemoveEqModel> RETAIL_ALL = (tester, queue, random) ->
            queue.retainAll(tester.randomElement(random));
    /* package-private */ static final Queues.LinearTester<RemoveEqModel> REMOVE_EQ_TEST = randomOf(REMOVE_ALL, RETAIL_ALL);

    // === Reflection

    /* package-private */ interface ReflectionModel extends QueueModel {
        Field ELEMENTS = getField("elements");
        Field HEAD = getField("head");

        @SuppressWarnings("unchecked")
        private <Z> Z get(final Field field) {
            try {
                return (Z) field.get(model());
            } catch (final IllegalAccessException e) {
                throw new AssertionError("Cannot access field " + field.getName() + ": " + e.getMessage(), e);
            }
        }

        private static Field getField(final String name) {
            try {
                final Field field = ArrayDeque.class.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (final NoSuchFieldException e) {
                throw new AssertionError("Reflection error: " + e.getMessage(), e);
            }
        }

        @ReflectionTest.Ignore
        default int head() {
            return get(HEAD);
        }

        @ReflectionTest.Ignore
        default Object[] elements() {
            return get(ELEMENTS);
        }

        @ReflectionTest.Ignore
        default <R> R reduce(final R zero, final Predicate<Object> p, final BiFunction<R, Integer, R> f) {
            final int size = size();
            final Object[] elements = elements();
            final int head = head();
            R result = zero;
            for (int i = 0; i < size; i++) {
                if (p.test(elements[(head + i) % elements.length])) {
                    result = f.apply(result, i);
                }
            }
            return result;
        }

        @ReflectionTest.Ignore
        @SuppressWarnings("unused")
        default <R> R reduce(final R zero, final Object v, final BiFunction<R, Integer, R> f) {
            return reduce(zero, o -> Objects.equals(v, o), f);
        }
    }


    // === Deque

    /* package-private */ interface DequeModel extends QueueModel {
        default void push(final Object element) {
            model().addFirst(element);
        }

        @SuppressWarnings("UnusedReturnValue")
        default Object peek() {
            return model().getLast();
        }

        default Object remove() {
            return model().removeLast();
        }
    }

    /* package-private */ interface DequeChecker<M extends DequeModel> extends QueueChecker<M> {
        @Override
        default void add(final M queue, final Object element, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.add(queue, element, random);
            } else {
                queue.push(element);
            }
        }

        @Override
        default void check(final M queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.check(queue, random);
            } else {
                queue.peek();
            }
        }

        @Override
        default void remove(final M queue, final ExtendedRandom random) {
            if (random.nextBoolean()) {
                QueueChecker.super.remove(queue, random);
            } else {
                queue.remove();
            }
        }
    }
}
