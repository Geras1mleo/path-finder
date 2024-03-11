import opgave.PriorityQueue;
import opgave.QueueItem;
import oplossing.MyPriorityQueue;
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
                .setFactory(MyPriorityQueue::new)
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
        System.out.println("Running decreaseKey on skew heap:");
        for (Benchmark annotation : methodAnnotations) {
            Supplier<HeapWithList<Integer, String>> heapWithListSupplier = getHeapWithListSupplier(annotation.elementsCount());
            SkewHeap.reMergeRoot = annotation.reMergeRoot();
            MyPriorityQueue.useRecursiveMerge = annotation.recursive();
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
    /*
    Running decreaseKey on skew heap:
	=> elementsCount = 10000, operations = 10000, recursive = true
		=> sample size: 100	mean: 3.496ms	std-dev: 1.583ms
	=> elementsCount = 10000, operations = 10000, recursive = false
		=> sample size: 100	mean: 3.253ms	std-dev: 1.32ms
	=> elementsCount = 100000, operations = 100000, recursive = true
		=> sample size: 100	mean: 33.681ms	std-dev: 9.754ms
	=> elementsCount = 100000, operations = 100000, recursive = false
		=> sample size: 100	mean: 30.271ms	std-dev: 2.975ms
	=> elementsCount = 1000000, operations = 1000000, recursive = true
		=> sample size: 100	mean: 1062.349ms	std-dev: 245.492ms
	=> elementsCount = 1000000, operations = 1000000, recursive = false
		=> sample size: 100	mean: 981.315ms	std-dev: 226.595ms
    * */

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

/*Running random decreaseKey with poll on skew heap:
	=> elementsCount = 20000, operations = 10000, reMergeRoot = true
		=> mean: 21.301ms	std-dev: 4.74ms
	=> elementsCount = 50000, operations = 10000, reMergeRoot = true
		=> mean: 43.704ms	std-dev: 1.479ms
	=> elementsCount = 50000, operations = 30000, reMergeRoot = true
		=> mean: 122.509ms	std-dev: 2.223ms
	=> elementsCount = 20000, operations = 10000, reMergeRoot = false
		=> mean: 17.689ms	std-dev: 0.478ms
	=> elementsCount = 50000, operations = 10000, reMergeRoot = false
		=> mean: 43.315ms	std-dev: 1.056ms
	=> elementsCount = 50000, operations = 30000, reMergeRoot = false
		=> mean: 123.28ms	std-dev: 4.884ms*/

/*Running decreaseKey on skew heap:
	=> elementsCount = 10000, operations = 10000, reMergeRoot = false
		=> mean: 5.233ms	std-dev: 1.716ms
	=> elementsCount = 100000, operations = 10000, reMergeRoot = false
		=> mean: 8.393ms	std-dev: 1.624ms
	=> elementsCount = 1000000, operations = 10000, reMergeRoot = false
		=> mean: 17.503ms	std-dev: 5.804ms
	=> elementsCount = 100000, operations = 100000, reMergeRoot = false
		=> mean: 95.883ms	std-dev: 12.085ms
	=> elementsCount = 100000, operations = 100000, reMergeRoot = true
		=> mean: 101.048ms	std-dev: 19.212ms*/