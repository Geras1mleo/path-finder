import oplossing.SkewHeap;

import java.lang.annotation.*;
import java.lang.reflect.Method;

public class SkewHeapBenchmarksAddElement extends Benchmarks {
    public static void main(String[] args) throws NoSuchMethodException {
        new SkewHeapBenchmarksAddElement().runBenchSkewHeapAddElement();
    }

    public void runBenchSkewHeapAddElement() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("bench", int.class);
        RunSkewHeapAddElements[] methodAnnotations = method.getAnnotationsByType(RunSkewHeapAddElements.class);
        System.out.println("Running add operation on empty skew heap:");
        for (var annotation : methodAnnotations) {
            System.out.printf("\t=> elementsCount = %d\n", annotation.elementsCount());
            System.out.println("\t\t=> " + Stats.getStats(sampleTimes(
                    Object::new, a -> bench(annotation.elementsCount())
            )));
        }
    }

    @RunSkewHeapAddElements(elementsCount = 10)
    @RunSkewHeapAddElements(elementsCount = 100)
    @RunSkewHeapAddElements(elementsCount = 1000)
    @RunSkewHeapAddElements(elementsCount = 10_000)
    @RunSkewHeapAddElements(elementsCount = 100_000)
    @RunSkewHeapAddElements(elementsCount = 1_000_000)
    @RunSkewHeapAddElements(elementsCount = 10_000_000)
    private void bench(int elementsCount) {
        var heap = new SkewHeap<Integer, String>();

        for (int i = 0; i < elementsCount; i++) {
            heap.add(RG.nextInt(elementsCount), "");
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(RunSkewHeapAddElementsList.class)
    @interface RunSkewHeapAddElements {
        int elementsCount() default 0;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RunSkewHeapAddElementsList {
        RunSkewHeapAddElements[] value();
    }
}
// TODO chart
/*Running add operation on empty skew heap:
	=> elementsCount = 100000
		=> mean: 25.098ms	std-dev: 8.451ms
	=> elementsCount = 1000000
		=> mean: 381.797ms	std-dev: 82.579ms
	=> elementsCount = 2000000
		=> mean: 1042.539ms	std-dev: 122.15ms*/
