package opgave;

/**
 * A factory with the single purpose of creating a PriorityQueue.
 */
@FunctionalInterface
public interface PriorityQueueFactory {
    /**
     * Creates an empty PriorityQueue with type-parameters of the caller's choosing
     *
     * @return a PriorityQueue with decreaseKey functionality
     * @param <P> the class of the key determining the priority
     * @param <V> the class of the values in the PriorityQueue
     */
    <P extends Comparable<P>, V> PriorityQueue<P, V> create();
}
