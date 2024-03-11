package oplossing;

import opgave.PriorityQueue;
import opgave.QueueItem;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/*
* Additionally, Iacono [7] has shown that insertions
* require only constant amortized time; significant for those applications that entail many
* more insertions than deletions.
* */


/**
 * This implementation is described in the report of project
 */
public class PairingHeapOld<P extends Comparable<P>, V> implements PriorityQueue<P, V> {

    private PairingHeapNode root;
    private int size = 0; // Cached heap size

    public PairingHeapOld() {
    }

    @Override
    public QueueItem<P, V> add(P priority, V value) {
        HeapItem<P, V> item = new HeapItem<>(priority, value);
        size++;
        return addItem(item);
    }

    private HeapItem<P, V> addItem(HeapItem<P, V> item) {
        root = merge(root, new PairingHeapNode(item));
        root.siblings = null;
        return item;
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
        root = twoPassMergeNonRecursive(root.children);
        if (root != null)
            root.siblings = null;
        return polledItem;
    }

    private PairingHeapNode twoPassMerge(List<PairingHeapNode> roots) {
        if (roots.size() <= 1) {
            return roots.stream().findFirst().orElse(null);
        }
        var a = roots.removeFirst();
        var b = roots.removeFirst();
        return merge(merge(a, b), twoPassMerge(roots));
    }

    private PairingHeapNode twoPassMergeNonRecursive(List<PairingHeapNode> roots) {
        if (roots.isEmpty())
            return null;

        var stack = new Stack<PairingHeapNode>();
        while (roots.size() >= 2) {
            stack.push(merge(roots.removeFirst(), roots.removeFirst()));
        }
        if (roots.size() == 1) {
            stack.push(roots.removeFirst());
        }
        var heap = stack.pop();
        while (!stack.isEmpty()) {
            heap = merge(heap, stack.pop());
        }
        return heap;
    }

    @Override
    public int size() {
        return size;
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

        if (h1.item.getPriority().compareTo(h2.item.getPriority()) < 0) {
            h1.children.addFirst(h2);
            h2.siblings = h1.children;
            return h1;
        } else {
            h2.children.addFirst(h1);
            h1.siblings = h2.children;
            return h2;
        }
    }

    private class PairingHeapNode {

        private final HeapItem<P, V> item;
        private final List<PairingHeapNode> children;
        private List<PairingHeapNode> siblings;
        // Ik moet nu geen rekening houden met of deze node het meest linkse node is tijdens decrease-key

        private PairingHeapNode(HeapItem<P, V> item) {
            this.item = item;
            this.children = new LinkedList<>();
            item.setDecreaseKeyFunction(this::decreaseKey);
        }

        private void decreaseKey() {
            if (this == root)
                return;

            siblings.remove(this);
            addItem(item);
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
                item.children.forEach(stack::push);
            }
            return size;
        }
    }
}
