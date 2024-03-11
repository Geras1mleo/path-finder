import opgave.PriorityQueue;
import opgave.PriorityQueueFactory;
import opgave.QueueItem;
import oplossing.PairingHeap;
import oplossing.SkewHeap;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class HeapBenchmarksDecreaseKey extends Benchmarks {

    private Supplier<PriorityQueue<Integer, String>> factory;

    public static void main(String[] args) throws NoSuchMethodException {
        new HeapBenchmarksDecreaseKey()
                .setFactory(SkewHeap::new)
                .runBench();
//                .runBenchRootDecreaseKeyPoll();
//                .runBenchBigBoy()
//                .runBenchHeapRandomDecreaseKeyPoll();
    }

    public HeapBenchmarksDecreaseKey setFactory(Supplier<PriorityQueue<Integer, String>> factory) {
        this.factory = factory;
        return this;
    }

    public HeapBenchmarksDecreaseKey runBench() throws NoSuchMethodException {
        Method method = HeapBenchmarksDecreaseKey.class.getDeclaredMethod("bench", PriorityQueue.class, ArrayList.class, int.class);
        Benchmark[] methodAnnotations = method.getAnnotationsByType(Benchmark.class);
        System.out.println("Running decreaseKey on heap:");
        for (Benchmark annotation : methodAnnotations) {
            Supplier<HeapWithList<Integer, String>> heapWithListSupplier = getHeapWithListSupplier(annotation.elementsCount());
            SkewHeap.reMergeRoot = annotation.reMergeRoot();
            PairingHeap.useRecursiveMerge = annotation.recursive();
            System.out.printf("\t=> elementsCount = %d, operations = %d, recursive = %s\n",
                    annotation.elementsCount(),
                    annotation.operations(),
                    annotation.recursive());
            System.out.println("\t\t=> " + Stats.getStats(sampleTimes(
                    heapWithListSupplier,
                    record -> bench(
                            record.heap(),
                            record.list(),
                            annotation.operations())
            )));
        }
        return this;
    }

    public HeapBenchmarksDecreaseKey runBenchBigBoy() throws NoSuchMethodException {
        Method method = HeapBenchmarksDecreaseKey.class.getDeclaredMethod("benchBigBoy", PriorityQueue.class, ArrayList.class, int.class);
        Benchmark[] methodAnnotations = method.getAnnotationsByType(Benchmark.class);
        System.out.println("Running big big decreaseKey on skew heap:");
        for (Benchmark annotation : methodAnnotations) {
            Supplier<HeapWithList<Integer, String>> heapWithListSupplier = getHeapWithListSupplier(annotation.elementsCount());
            SkewHeap.reMergeRoot = annotation.reMergeRoot();
            System.out.printf("\t=> elementsCount = %d, operations = %d\n",
                    annotation.elementsCount(),
                    annotation.operations());
            System.out.println("\t\t=> " + Stats.getStats(sampleTimes(
                    heapWithListSupplier,
                    record -> benchBigBoy(
                            record.heap(),
                            record.list(),
                            annotation.operations())
            )));
        }
        return this;
    }

    public HeapBenchmarksDecreaseKey runBenchRootDecreaseKeyPoll() throws NoSuchMethodException {
        Method method = HeapBenchmarksDecreaseKey.class.getDeclaredMethod("benchRootDecreaseKeyPoll", PriorityQueue.class, ArrayList.class, int.class);
        Benchmark[] methodAnnotations = method.getAnnotationsByType(Benchmark.class);
        System.out.println("Running decreaseKey(of root) + poll on skew heap:");
        for (Benchmark annotation : methodAnnotations) {
            Supplier<HeapWithList<Integer, String>> heapWithListSupplier = getHeapWithListSupplier(annotation.elementsCount());
            SkewHeap.reMergeRoot = annotation.reMergeRoot();
            System.out.printf("\t=> elementsCount = %d, operations = %d, remerge = %s\n",
                    annotation.elementsCount(),
                    annotation.operations(),
                    annotation.reMergeRoot());
            System.out.println("\t\t=> " + Stats.getStats(sampleTimes(
                    heapWithListSupplier,
                    record -> benchRootDecreaseKeyPoll(
                            record.heap(),
                            record.list(),
                            annotation.operations())
            )));
        }
        return this;
    }

    public HeapBenchmarksDecreaseKey runBenchHeapRandomDecreaseKeyPoll() throws NoSuchMethodException {
        Method method = HeapBenchmarksDecreaseKey.class.getDeclaredMethod("benchHeapRandomDecreaseKeyPoll", PriorityQueue.class, ArrayList.class, int.class);
        Benchmark[] methodAnnotations = method.getAnnotationsByType(Benchmark.class);
        System.out.println("Running random decreaseKey with poll on skew heap:");
        for (Benchmark annotation : methodAnnotations) {
            SkewHeap.reMergeRoot = annotation.reMergeRoot();
            Supplier<HeapWithList<Integer, String>> heapWithListSupplier = getHeapWithListSupplier(annotation.elementsCount());
            System.out.printf("\t=> elementsCount = %d, operations = %d, reMergeRoot = %s\n",
                    annotation.elementsCount(),
                    annotation.operations(),
                    annotation.reMergeRoot());
            System.out.println("\t\t=> " + Stats.getStats(sampleTimes(
                    heapWithListSupplier,
                    record -> benchHeapRandomDecreaseKeyPoll(
                            record.heap(),
                            record.list(),
                            annotation.operations())
            )));
        }
        return this;
    }

    private Supplier<HeapWithList<Integer, String>> getHeapWithListSupplier(int elementsCount) {
        return () -> {
            var list = new ArrayList<QueueItem<Integer, String>>();
            var heap = factory.get();
            int lowerBound = elementsCount >> 3;

            for (int i = 0; i < elementsCount; i++) {
                list.add(heap.add(RG.nextInt(lowerBound, elementsCount), ""));
            }
            return new HeapWithList<>(heap, list);
        };
    }

    @Benchmark(elementsCount = 100, operations = 100, recursive = true)
    @Benchmark(elementsCount = 100, operations = 100, recursive = false)
    @Benchmark(elementsCount = 1000, operations = 1000, recursive = true)
    @Benchmark(elementsCount = 1000, operations = 1000, recursive = false)
    @Benchmark(elementsCount = 10_000, operations = 10_000, recursive = true)
    @Benchmark(elementsCount = 10_000, operations = 10_000, recursive = false)
    @Benchmark(elementsCount = 100_000, operations = 100_000, recursive = true)
    @Benchmark(elementsCount = 100_000, operations = 100_000, recursive = false)
    @Benchmark(elementsCount = 1_000_000, operations = 1_000_000, recursive = true)
    @Benchmark(elementsCount = 1_000_000, operations = 1_000_000, recursive = false)
    private void bench(PriorityQueue<Integer, String> heap,
                       ArrayList<QueueItem<Integer, String>> list,
                       int operations) {
        Set<QueueItem<Integer, String>> removedSet = new HashSet<>();
        for (int i = 0; i < operations; i++) {
            var item = list.get(RG.nextInt(list.size()));
            if (item.getPriority() > 0) {
                if (!removedSet.contains(item)) {
                    item.decreaseKey(RG.nextInt(item.getPriority()));
                }
            } else {
                removedSet.add(heap.poll()); // Met 1_000_000 operaties zal deze ongeveer 15-20 keer pollen
            }
        }
    }

    @Benchmark(elementsCount = 1000, operations = 100, reMergeRoot = false)
    @Benchmark(elementsCount = 1000, operations = 100, reMergeRoot = true)
    @Benchmark(elementsCount = 5000, operations = 500, reMergeRoot = false)
    @Benchmark(elementsCount = 5000, operations = 500, reMergeRoot = true)
    @Benchmark(elementsCount = 10_000, operations = 1000, reMergeRoot = false)
    @Benchmark(elementsCount = 10_000, operations = 1000, reMergeRoot = true)
    @Benchmark(elementsCount = 50_000, operations = 5000, reMergeRoot = false)
    @Benchmark(elementsCount = 50_000, operations = 5000, reMergeRoot = true)
    @Benchmark(elementsCount = 100_000, operations = 10_000, reMergeRoot = false)
    @Benchmark(elementsCount = 100_000, operations = 10_000, reMergeRoot = true)
    @Benchmark(elementsCount = 500_000, operations = 50_000, reMergeRoot = false)
    @Benchmark(elementsCount = 500_000, operations = 50_000, reMergeRoot = true)
    private void benchRootDecreaseKeyPoll(PriorityQueue<Integer, String> heap,
                                          ArrayList<QueueItem<Integer, String>> list,
                                          int operations) {
        Set<QueueItem<Integer, String>> removedSet = new HashSet<>();
        int counter = 0;
        for (int i = 0; i < operations; i++) {
            if (counter % 3 == 0) {
                var item = heap.peek();
                if (item.getPriority() > 0)
                    item.decreaseKey(RG.nextInt(item.getPriority()));
                removedSet.add(heap.poll());
            } else {
                var item = list.get(RG.nextInt(list.size()));
                if (!removedSet.contains(item) && item.getPriority() > 0) {
                    item.decreaseKey(RG.nextInt(item.getPriority()));
                }
            }

            counter++;
        }
        /*
         * SkewHeap: Wanneer wij na de decrease key van root de heap re-mergen dan weten wij dat de volgende add of poll bewerking zeker
         * in constante tijd wordt uitgevoerd
         * */
    }

    @Benchmark(elementsCount = 20_000, operations = 10_000, reMergeRoot = true)
    @Benchmark(elementsCount = 50_000, operations = 10_000, reMergeRoot = true)
    @Benchmark(elementsCount = 50_000, operations = 30_000, reMergeRoot = true)
    @Benchmark(elementsCount = 20_000, operations = 10_000, reMergeRoot = false)
    @Benchmark(elementsCount = 50_000, operations = 10_000, reMergeRoot = false)
    @Benchmark(elementsCount = 50_000, operations = 30_000, reMergeRoot = false)
    private void benchHeapRandomDecreaseKeyPoll(PriorityQueue<Integer, String> heap,
                                                ArrayList<QueueItem<Integer, String>> list,
                                                int operations) {
        for (int i = 0; i < operations; i++) {
            if (RG.nextInt(3) != 0) {
                // Chance 2/3 to decrease key
                if (RG.nextInt(5) != 0) {
                    // Chance 4/5 to decrease random key
                    var item = list.get(RG.nextInt(list.size()));
                    item.decreaseKey(RG.nextInt(item.getPriority()));
                } else {
                    // Chance 1/5 to decrease the root of the heap
                    var item = heap.peek();
                    if (item.getPriority() != 0) {
                        item.decreaseKey(RG.nextInt(item.getPriority()));
                    }
                }
            } else {
                // Chance 1/3 to poll item
                var item = heap.poll();
                list.remove(item);
            }
        }
    }

    @Benchmark(elementsCount = 1_000_000, operations = 1_000_000)
    public void benchBigBoy(PriorityQueue<Integer, String> heap,
                            ArrayList<QueueItem<Integer, String>> list,
                            int operations) {
        for (int i = 0; i < operations; i++) {
            var randomItem = list.get(RG.nextInt(list.size()));
            if (randomItem.getPriority() > 0) {
                randomItem.decreaseKey(RG.nextInt(randomItem.getPriority()));
            }
        }
    }

    record HeapWithList<P extends Comparable<P>, V>(PriorityQueue<P, V> heap, ArrayList<QueueItem<P, V>> list) {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(Benchmarks.class)
    @interface Benchmark {
        int elementsCount() default 0;

        int operations() default 0;

        boolean reMergeRoot() default false;

        boolean recursive() default true;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Benchmarks {
        Benchmark[] value();
    }
}
