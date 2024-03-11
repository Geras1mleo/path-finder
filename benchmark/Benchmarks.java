import java.text.DecimalFormat;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Benchmarks {

    public final static Random RG = new Random(69);
    public final static DecimalFormat df = new DecimalFormat("#.###");

    public <T> long[] sampleTimes(Supplier<T> supplier, Consumer<T> benchedConsumer, int repetitions) {
        int warmupReps = 5;
        long[] times = new long[repetitions];

        // Warm up:
        for (int i = 0; i < warmupReps; i++) {
            benchedConsumer.accept(supplier.get());
        }

        // Actual run:
        for (int i = 0; i < repetitions; i++) {
            T value = supplier.get();
            times[i] = timeExecution(() -> benchedConsumer.accept(value));
        }
        return times;
    }
    
    public <T> long[] sampleTimes(Supplier<T> supplier, Consumer<T> benchedConsumer) {
        int repetitions = 100;
        return sampleTimes(supplier, benchedConsumer, repetitions);
    }

    public long timeExecution(Runnable runnable) {
        var start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;
    }
}
