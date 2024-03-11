import java.util.Arrays;

public record Stats(int sampleSize, double mean, double stdDev) {

    public static Stats getStats(long[] nanoTimes) {
        double mean = (double) (Arrays.stream(nanoTimes).sum() / nanoTimes.length) / 1_000_000.0;
        double stdDev = calculateSD(nanoTimes) / 1_000_000.0;
        return new Stats(nanoTimes.length, mean, stdDev);
    }

    // Gejat van https://www.programiz.com/java-programming/examples/standard-deviation
    public static double calculateSD(long[] numArray) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for (long num : numArray) {
            sum += num;
        }
        double mean = sum / length;

        for (double num : numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    @Override
    public String toString() {
        return "sample size: " + sampleSize 
                + "\tmean: " + Benchmarks.df.format(mean) + "ms"
                + "\tstd-dev: " + Benchmarks.df.format(stdDev) + "ms";
    }
}
