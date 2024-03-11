import opgave.PriorityQueue;
import opgave.QueueItem;
import oplossing.PairingHeap;
import oplossing.SkewHeap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestHeap {

    private final Random RG = new Random(69);

    private final Supplier<PriorityQueue<Integer, String>> factory = SkewHeap::new;
//    private final Supplier<PriorityQueue<Integer, String>> factory = PairingHeap::new;
//    private final Supplier<PriorityQueue<Integer, String>> factory = MyPriorityQueueOld::new;

    @Test
    public void testSizeSmall() {
        // Arrange:
        int size = 10;
        var heap = factory.get();

        // Act:
        for (int i : IntStream.generate(() -> RG.nextInt(100)).limit(size).toArray()) {
            heap.add(i, "");
        }

        // Assert
        assertEquals(size, heap.size());
    }

    @Test
    public void testSizeBig() {
        // Arrange:
        var heap = factory.get();
        int expectedSize = 1_000_000;

        // Act:
        for (int i = 0; i < expectedSize; i++) {
            int prior = RG.nextInt(0, 50);
            var value = String.valueOf((char) (65 + (prior % 26)));
            heap.add(prior, value);
        }

        // Assert:
        assertEquals(expectedSize, heap.size()); // Tested with recursive size variant
    }

    @Test
    public void testSizeBigDescendingOrder() {
        // Arrange:
        var heap = factory.get();
        int expectedSize = 1_000_000;

        // Act:
        for (int i = 0; i < expectedSize; i++) {
            int prior = expectedSize - i;
            var value = String.valueOf((char) (65 + (prior % 26)));
            heap.add(prior, value);
        }

        // Assert:
        assertEquals(expectedSize, heap.size());
    }

    @Test
    public void testIntegrationAddPollSize() {
        // Arrange:
        var actualHeap = factory.get();
        var expectedSize = 0;
        int operations = 1_000_000;

        // Act:
        for (int i = 0; i < operations; i++) {
            if (RG.nextInt() % 4 == 0) {
                // chance 1/4 to poll
                actualHeap.poll();
                expectedSize--;
            } else {
                int prior = RG.nextInt(0, 50);
                var value = String.valueOf((char) (65 + (prior % 26)));
                actualHeap.add(prior, value);
                expectedSize++;
            }
        }

        int actualSize = actualHeap.size();

        // Assert:
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void testPoll() {
        // Arrange:
        var heap = factory.get();
        var item1 = heap.add(5, "");
        var item2 = heap.add(6, "");

        // Act:
        var polled = heap.poll();

        // Assert:
        assertEquals(1, heap.size());
        assertEquals(item1, polled);
        assertEquals(item2.getPriority(), heap.poll().getPriority());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testPollEmpty() {
        // Arrange:
        var heap = factory.get();

        // Assert:
        Assertions.assertThrows(RuntimeException.class, heap::poll);
    }

    @Test
    public void testPollOneElement() {
        // Arrange:
        var heap = factory.get();
        var item = heap.add(5, "value");

        // Assert:
        assertEquals(item.getPriority(), heap.poll().getPriority());
    }

    @Test
    public void testPollCorrectOrder() {
        // Arrange:
        var heap = factory.get();
        var actualSize = 0;
        int add = 1_000_000;
        int remove = 50_000;

        // Act:
        var list = new ArrayList<Integer>();
        for (int i = 0; i < add; i++) {
            int prior = RG.nextInt(0, add);
            heap.add(prior, null);
            list.add(prior);
        }

        list.sort(Integer::compareTo);

        Integer[] least_elements = list.subList(0, remove).toArray(Integer[]::new);
        Integer[] actual_elements = new Integer[remove];

        for (int i = 0; i < remove; i++) {
            actual_elements[i] = heap.poll().getPriority();
        }

        actualSize = heap.size();

        // Assert:
        assertEquals(add - remove, actualSize);
        for (int i = 0; i < remove; i++) {
            assertEquals(least_elements[i], actual_elements[i]);
        }
    }

    @Test
    public void testDecreaseKeySmallHeap() {
        // Arrange:
        var heap = factory.get();
        heap.add(5, "");
        heap.add(8, "");
        heap.add(6, "");
        heap.add(15, "");
        var item = heap.add(20, "next-value:3");
        int newPriority = 3;

        // Act:
        item.decreaseKey(newPriority);
        var polled = heap.poll();

        // Assert:
        assertEquals(item, polled); // Referentie naar Item wordt bewaard/teruggegeven
        assertEquals(newPriority, polled.getPriority()); // Item is min-element geworden
    }

    @Test
    public void testDecreaseKeyTopElement() {
        // Arrange:
        var heap = factory.get();
        var item = heap.add(3, "test");

        // Act:
        item.decreaseKey(1);

        // Assert:
        assertEquals(1, item.getPriority());
    }

    @Test
    public void testDecreaseThrows() {
        // Arrange:
        var heap = factory.get();
        var item = heap.add(3, "");
        var item2 = heap.add(4, "");

        // Act:
        heap.poll();

        // Assert:
        Assertions.assertThrows(IllegalStateException.class, () -> item.decreaseKey(1)); // Decrease key op een verwijderde item
        Assertions.assertThrows(IllegalArgumentException.class, () -> item2.decreaseKey(6)); // Decrease key met hogere priority
    }

    @Test
    public void testDecreaseOperations() {
        // Arrange:
        var heap = factory.get();
        var list = new ArrayList<QueueItem<Integer, String>>();
        int elemCount = 20;

        // Act:
        for (int i = 0; i < elemCount; i++) {
            var item = heap.add(RG.nextInt(0, elemCount), "");
            list.add(item);
        }

        for (QueueItem<Integer, String> item : list) {
            item.decreaseKey(item.getPriority() - 1);
            // Assert:
            assertEquals(elemCount, heap.size());
        }
    }

    @Test
    public void testManyDecreaseOperations() {
        // Arrange:
        var heap = factory.get();
        var list = new ArrayList<QueueItem<Integer, String>>();
        int elemCount = 1_000_000;

        // Act:
        for (int i = 0; i < elemCount; i++) {
            var item = heap.add(RG.nextInt(0, elemCount), "");
            list.add(item);
        }

        for (int i = 0; i < 2; i++) {
            for (QueueItem<Integer, String> item : list) {
                item.decreaseKey(item.getPriority() - 1);
            }
        }

        // Assert:
        assertEquals(elemCount, heap.size());
        // This test tests if there won't be any exceptions while decreasing many keys and whether it is not too slow
    }

    @Test
    public void testRandomOperations() {
        // Arrange:
        var heap = factory.get();
        var list = new ArrayList<QueueItem<Integer, String>>();
        int elemCount = 200_000;
        int operationsCount = 100_000;
        int removedCount = 0;

        for (int i = 0; i < elemCount; i++) {
            var item = heap.add(RG.nextInt(0, elemCount), RG.nextInt(1000) + "");
            list.add(item);
        }

        // Act:
        for (int i = 0; i < operationsCount; i++) {
            if (RG.nextBoolean()) {
                var item = list.get(RG.nextInt(list.size()));
                item.decreaseKey(RG.nextInt(item.getPriority()));
            } else {
                var item = heap.poll();
                list.remove(item);
                removedCount++;
            }
        }

        // Assert:
        assertEquals(elemCount - removedCount, heap.size());
    }

    @Test
    public void testSkewWithReMerge() {
        // Arrange:
        SkewHeap.reMergeRoot = true;
        var heap = new SkewHeap<Integer, String>();
        var item = heap.add(5, "");
        heap.add(6, "");
        heap.add(9, "");
        heap.add(15, "");

        // Act:
        item.decreaseKey(3);
        heap.poll();

        // Assert:
        assertTrue(heap.isSkewHeap());
    }
}
