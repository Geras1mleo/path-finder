package oplossing;

import opgave.PriorityQueue;
import opgave.QueueItem;

import java.util.Stack;

/**
 * Pairing heap implementation
 * <a href="https://en.wikipedia.org/wiki/Pairing_heap">Source</a>
 */
public class PairingHeap<P extends Comparable<P>, V> implements PriorityQueue<P, V> {

    /**
     * Use Iterative <> Cached size
     */
    public static boolean useCachedSize = false;
    public static boolean useRecursiveMerge = false;

    private PairingHeapNode root;
    private int size = 0; // Cached heap size

    public PairingHeap() {
    }

    @Override
    public QueueItem<P, V> add(P priority, V value) {
        HeapItem<P, V> item = new HeapItem<>(priority, value);
        size++;
        return addItem(item);
    }

    private HeapItem<P, V> addItem(HeapItem<P, V> item) {
        root = merge(root, new PairingHeapNode(item));
        root.parent = null;
        return item;
    }

    private void addSubTree(PairingHeapNode subTree) {
        root = merge(root, subTree);
        root.parent = null;
    }

    @Override
    public QueueItem<P, V> peek() {
        return root == null ? null : root.item;
    }

    @Override
    public QueueItem<P, V> poll() {
        if (root == null) {
            throw new RuntimeException("Heap is empty");
        }
        var polledItem = root.item;
        polledItem.setDecreaseKeyFunction(null);
        size--;

        if (useRecursiveMerge)
            root = twoPassMerge(root.child);
        else
            root = twoPassMergeNonRecursive(root.child);

        if (root != null)
            root.parent = null;
        return polledItem;
    }

    private PairingHeapNode twoPassMerge(PairingHeapNode node) {
        if (node == null || node.rightNeighbor == null)
            return node;

        var nextNode = node.rightNeighbor;
        var remainingNodes = node.rightNeighbor.rightNeighbor;

        node.rightNeighbor = null;
        nextNode.rightNeighbor = null;

        return merge(merge(node, nextNode), twoPassMerge(remainingNodes));
    }

    private PairingHeapNode twoPassMergeNonRecursive(PairingHeapNode node) {
        if (node == null || node.rightNeighbor == null)
            return node;

        var stack = new Stack<PairingHeapNode>();
        while (node != null && node.rightNeighbor != null) {
            var nextNode = node.rightNeighbor;
            var remainingNode = node.rightNeighbor.rightNeighbor;

            node.rightNeighbor = null;
            nextNode.rightNeighbor = null;

            stack.push(merge(node, nextNode));

            node = remainingNode;
        }
        if (node != null) {
            stack.push(node);
        }
        var heap = stack.pop();
        while (!stack.isEmpty()) {
            heap = merge(heap, stack.pop());
        }
        return heap;
    }

    @Override
    public int size() {
        if (useCachedSize) return size;
        if (root != null) return root.sizeNonRecursive();
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    private PairingHeapNode merge(PairingHeapNode h1, PairingHeapNode h2) {
        if (h1 == null)
            return h2;
        if (h2 == null)
            return h1;

        PairingHeapNode small, large;
        if (h1.item.getPriority().compareTo(h2.item.getPriority()) < 0) {
            small = h1;
            large = h2;
        } else {
            small = h2;
            large = h1;
        }

        large.rightNeighbor = small.child;
        large.leftNeighbor = null;
        large.parent = small;
        if (small.child != null)
            small.child.leftNeighbor = large;
        small.child = large;
        small.leftNeighbor = null;
        small.rightNeighbor = null;
        return small;
    }

    private class PairingHeapNode {

        private final HeapItem<P, V> item;
        private PairingHeapNode child;
        private PairingHeapNode leftNeighbor;
        private PairingHeapNode rightNeighbor;
        private PairingHeapNode parent;
        // Ik moet nu geen rekening houden met of deze node het meest linkse node is tijdens decrease-key

        private PairingHeapNode(HeapItem<P, V> item) {
            this.item = item;
            item.setDecreaseKeyFunction(this::decreaseKey);
        }

        private void decreaseKey() {
            if (this == root)
                return;

            if (parent.item.getPriority().compareTo(item.getPriority()) < 0)
                return;

            if (leftNeighbor != null) {
                leftNeighbor.rightNeighbor = rightNeighbor;
            } else {
                parent.child = rightNeighbor;
            }
            if (rightNeighbor != null)
                rightNeighbor.leftNeighbor = leftNeighbor;

            addSubTree(this);
        }

        private int sizeNonRecursive() {
            // Recursief size veroorzaakt vaak een StackOverflow exception
            // Deze size wordt gebruikt voor testing doeleinden
            var stack = new Stack<PairingHeapNode>();
            stack.push(this);
            int size = 0;
            while (!stack.isEmpty()) {
                var item = stack.pop();
                size++;
                if (item.rightNeighbor != null)
                    stack.push(item.rightNeighbor);
                if (item.child != null)
                    stack.push(item.child);
            }
            return size;
        }
    }
}
