package opgave;

/**
 * Interface for a queued item in a priority queue which can be updated.
 *
 * @param <P> the type of the priority
 * @param <V> the type of the value
 */
public interface QueueItem<P extends Comparable<P>, V> {

    /**
     * Returns the priority of this item.
     *
     * @return the priority of this item
     */
    P getPriority();

    /**
     * Returns the value of this item.
     *
     * @return the value of this item
     */
    V getValue();

    /**
     * Increases the priority of this item by decreasing its key value.
     * Only this item's priority in the priority queue is updated, not the priority of other items with the same value.
     *
     * @param newPriority the new priority, should be equal to or smaller than the current priority
     * @throws IllegalStateException if the current QueueItem is not present in the PriorityQueue anymore
     * @throws IllegalArgumentException if the newPriority is ordered <b>after</b> the current priority (e.g. the key is increased)
     */
    void decreaseKey(P newPriority);
}
