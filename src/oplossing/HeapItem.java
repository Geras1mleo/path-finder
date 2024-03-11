package oplossing;

import opgave.QueueItem;

public class HeapItem<P extends Comparable<P>, V> implements QueueItem<P, V> {

    private P priority;
    private final V value;

    private Runnable decreaseKeyFunction;

    public HeapItem(P priority, V value) {
        this.priority = priority;
        this.value = value;
    }

    @Override
    public P getPriority() {
        return priority;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void decreaseKey(P newPriority) {
        if (decreaseKeyFunction == null)
            throw new IllegalStateException("Item has been removed from the heap...");

        if (priority.compareTo(newPriority) < 0) {
            throw new IllegalArgumentException("New priority should be less (or equal) than current priority...");
        }

        this.priority = newPriority;
        decreaseKeyFunction.run();
    }

    @Override
    public String toString() {
        return "Item{" +
                "priority=" + priority +
                ",value=" + value +
                '}';
    }

    void setDecreaseKeyFunction(Runnable decreaseKeyFunction) {
        this.decreaseKeyFunction = decreaseKeyFunction;
    }
}
