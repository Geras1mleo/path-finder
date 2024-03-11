package oplossing;

import opgave.PriorityQueue;

import java.util.Objects;
import java.util.Stack;

public class SkewHeap<P extends Comparable<P>, V> implements PriorityQueue<P, V> {

    /**
     * Use Iterative <> Cached size
     */
    public static boolean useCachedSize = false;
    private int size = 0; // Cached heap size

    private HeapItem<P, V> item;
    private SkewHeap<P, V> parent;
    private SkewHeap<P, V> left;
    private SkewHeap<P, V> right;

    public static boolean reMergeRoot = false;

    public SkewHeap() {
    }

    /**
     * Made public for testing purposes
     */
    private SkewHeap(SkewHeap<P, V> parent, SkewHeap<P, V> left, SkewHeap<P, V> right, HeapItem<P, V> item) {
        initialize(parent, left, right, item);
    }

    private void initialize(SkewHeap<P, V> parent, SkewHeap<P, V> left, SkewHeap<P, V> right, HeapItem<P, V> item) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        setParent();
        setItem(item);
    }

    @Override
    public int size() {
        if (useCachedSize) return size;
        else return sizeNonRecursive();
    }

    private int sizeNonRecursive() {
        var stack = new Stack<SkewHeap<P, V>>();
        stack.push(this);
        int size = 0;
        while (!stack.isEmpty()) {
            var item = stack.pop();
            size++;
            if (item.right != null)
                stack.push(item.right);
            if (item.left != null)
                stack.push(item.left);
        }
        return size;
    }

    private int sizeRecursive() {
        var thisSize = item == null ? 0 : 1;
        var leftSize = left == null ? 0 : left.size();
        var rightSize = right == null ? 0 : right.size();
        return thisSize + leftSize + rightSize;
    }

    @Override
    public boolean isEmpty() {
        return item == null;
    }

    @Override
    public HeapItem<P, V> peek() {
        return item;
    }

    @Override
    public HeapItem<P, V> add(P priority, V value) {
        size++;
        if (item == null) {
            return setItem(priority, value);
        }
        return addItem(createItem(priority, value));
    }

    @Override
    public HeapItem<P, V> poll() {
        if (item == null) {
            throw new RuntimeException("Skew heap is empty");
        }
        size--;
        HeapItem<P, V> returnItem = item;
        SkewHeap<P, V> newHeap;
        if (right == null) {
            newHeap = left;
        } else {
            newHeap = merge(left, right);
        }
        if (newHeap != null) {
            setItem(newHeap.item);
            this.left = newHeap.left;
            this.right = newHeap.right;
            setParent();
        } else {
            item = null;
        }
        return unsetItem(returnItem);
    }

    private void decreaseKey() {
        // When parent is null => this node is root:
        // When reMergeRoot == true => even when decreased key was in root the whole heap will be re-merged
        // We will further look if it improves overall amortised complexity of operations (no)
        // We know exactly that poll-operation after decrease of root (with re-merge) is O(1)
        if (parent != null || reMergeRoot) {
            if (parent != null && parent.item.getPriority().compareTo(item.getPriority()) < 0)
                return; // heap condition is ok => no action needed

            var itemToAdd = this.item;
            var mergedSkew = (SkewHeap<P, V>) left;
            if (right != null) {
                mergedSkew = merge(left, right);
            }
            if (parent != null) {
                parent.replaceChild(this, mergedSkew);
            } else if (mergedSkew != null) {
                initialize(null, mergedSkew.left, mergedSkew.right, mergedSkew.item);
            }
            var root = (SkewHeap<P, V>) getRoot(); // TODO: dure operatie?
            root.addItem(itemToAdd);
        }
    }

    private HeapItem<P, V> createItem(P priority, V value) {
        return new HeapItem<>(priority, value);
    }

    private void swap() {
        var temp = left;
        left = right;
        right = temp;
    }

    private HeapItem<P, V> unsetItem(HeapItem<P, V> item) {
        item.setDecreaseKeyFunction(null);
        return item;
    }

    private HeapItem<P, V> setItem(P priority, V value) {
        var newItem = createItem(priority, value);
        return setItem(newItem);
    }

    // THE ONLY ASSIGNMENT FOR item FIELD!!!
    // ALWAYS UPDATING NODE! (for item.decreaseKey to always call its owner!!)
    private HeapItem<P, V> setItem(HeapItem<P, V> item) {
        this.item = item;
        item.setDecreaseKeyFunction(this::decreaseKey);
        return item;
    }

    private void setParent() {
        if (this.left != null)
            this.left.parent = this;
        if (this.right != null)
            this.right.parent = this;
    }

    private HeapItem<P, V> addItem(HeapItem<P, V> newItem) {
        if (item.getPriority().compareTo(newItem.getPriority()) <= 0) {
            if (right != null) {
                newItem = right.addItem(newItem);
            } else {
                right = new SkewHeap<>(this, null, null, newItem);
            }
            swap();
        } else {
            if (parent != null) {
                parent.right = new SkewHeap<>(parent, this, null, newItem);
            } else {
                var newRoot = new SkewHeap<>(this, left, right, item);
                setItem(newItem);
                this.left = newRoot;
                this.right = null;
            }
        }

        return newItem;
    }

    private static <P extends Comparable<P>, V> SkewHeap<P, V> merge(SkewHeap<P, V> h1, SkewHeap<P, V> h2) {
        SkewHeap<P, V> small, large;
        if (h1.item.getPriority().compareTo(h2.item.getPriority()) <= 0) {
            small = h1;
            large = h2;
        } else {
            small = h2;
            large = h1;
        }
        small.swap();
        if (small.left == null) {
            small.left = large;
        } else {
            small.left = merge(small.left, large);
        }
        small.setParent();
        return small;
    }

    public boolean isSkewHeap() { // For testing purposes
        boolean isSkewHeap = right == null || left != null;
        if (left != null)
            isSkewHeap = left.isSkewHeap();
        if (right != null)
            isSkewHeap &= right.isSkewHeap();
        return isSkewHeap;
    }

    private void replaceChild(SkewHeap<P, V> replacedChild, SkewHeap<P, V> newChild) {
        if (replacedChild == left)
            left = newChild;
        else if (replacedChild == right)
            right = newChild;
        setParent();
        if (right != null && left == null) {
            swap();
        }
    }

    protected SkewHeap<P, V> getRoot() {
        if (this.parent == null)
            return this;
        else return parent.getRoot();
    }

    @Override
    public String toString() {
        return "Heap{" +
                "item=" + item +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkewHeap<?, ?> heap)) return false;
        return Objects.equals(item, heap.item)
                && Objects.equals(left, heap.left)
                && Objects.equals(right, heap.right);
    }
}
