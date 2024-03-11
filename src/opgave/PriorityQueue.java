package opgave;

/**
 * Interface for a priority queue with update functionality.
 *
 * The queue may contain multiple items with the same priority, the same value, or both.
 *
 * @see QueueItem for the semantics of updating an item.
 *
 * @param <P> the type of the priority
 * @param <V> the type of the value
 */
public interface PriorityQueue<P extends Comparable<P>, V> {

    /**
     * Adds a new item to the priority queue.
     * It is allowed to add the same value multiple times (with the same or different priorities).
     *
     * @param priority the item to be added
     * @param value the value of the item
     * @return the item that was added
     */
    QueueItem<P, V> add(P priority, V value);

    /**
     * Returns the item with the highest priority without removing it from the queue.
     *
     * @return the item with the highest priority, or null if the queue is empty
     */
    QueueItem<P, V> peek();

    /**
     * Removes and returns the item with the highest priority.
     *
     * @return the item with the highest priority, or null if the queue is empty
     */
    QueueItem<P, V> poll();

    /**
     * Returns the number of items in the queue.
     *
     * @return the number of items in the queue
     */
    int size();

    /**
     * Returns whether the queue is empty.
     *
     * @return true if the queue is empty, false otherwise
     */
    boolean isEmpty();
}
