import opgave.DirectedEdge;
import opgave.Node;
import opgave.PriorityQueueFactory;
import oplossing.PairingHeap;
import oplossing.AStar;
import oplossing.SkewHeap;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

import static oplossing.GraphMaker.*;


/*
 * Benchmarks on complete graphs makes little sense for A*
 * */
public class ShortestPathBenchmarks extends Benchmarks {

    private PriorityQueueFactory factory;


    public static void main(String[] args) throws NoSuchMethodException {
        new ShortestPathBenchmarks()
                .setFactory(SkewHeap::new)
                .runBench();
    }

    public ShortestPathBenchmarks setFactory(PriorityQueueFactory factory) {
        this.factory = factory;
        return this;
    }

    public void runBench() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("bench", AStar.class, Node.class, Node.class);
        RunShortestPath[] methodAnnotations = method.getAnnotationsByType(RunShortestPath.class);
        System.out.println("Running shortest path on 2 nodes:");


        for (var annotation : methodAnnotations) {
            var ref = new Object() {
                boolean completeGraphInitialized = false;
                MspHelper msp;
            };
            Supplier<MspHelper> mspHelperSupplier = () -> {
                if (!ref.completeGraphInitialized) {
                    ref.msp = getMyShortestPath(annotation.nodes(), annotation.edges());
                    ref.completeGraphInitialized = true;
                }
                return ref.msp;
            };
            mspHelperSupplier.get();
            System.out.printf("\t=> nodes = %d, edges = %d\n", annotation.nodes(), ref.msp.edges.size());
            System.out.println("\t\t=> " + Stats.getStats(sampleTimes(
                    mspHelperSupplier, msp -> {
                        Node from = msp.nodes().get(RG.nextInt(msp.nodes().size()));
                        Node to = msp.nodes().get(RG.nextInt(msp.nodes().size()));
                        bench(msp.msp(), from, to);
                    }, 10)));
        }
    }


    private MspHelper getMyShortestPath(int nodes, int edges) {
        var msp = new AStar();
        msp.setPriorityQueueFactory(factory);

        var nodesList = new ArrayList<Node>();
        var edgesSet = new HashSet<DirectedEdge>();

        makeCoherentGraph(nodes, edges, nodesList, edgesSet);

        msp.setGraph(nodesList, edgesSet.stream().toList());
        return new MspHelper(msp, nodesList, edgesSet);
    }

    // Edges count will increase up to at least 2 * nodes count because we need a coherent graph
    @RunShortestPath(nodes = 10_000, edges = 100)
    @RunShortestPath(nodes = 10_000, edges = 1_000)
    @RunShortestPath(nodes = 10_000, edges = 10_000)
    @RunShortestPath(nodes = 10_000, edges = 100_000)
    @RunShortestPath(nodes = 1_000_000, edges = 100)
    @RunShortestPath(nodes = 1_000_000, edges = 1_000)
    @RunShortestPath(nodes = 1_000_000, edges = 10_000)
    @RunShortestPath(nodes = 1_000_000, edges = 100_000)
    @RunShortestPath(nodes = 5_000_000, edges = 1_000_000)
    private void bench(AStar msp, Node from, Node to) {
        msp.shortestPath(from, to);
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(RunShortestPaths.class)
    @interface RunShortestPath {
        int nodes();

        int edges();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface RunShortestPaths {
        RunShortestPath[] value();
    }

    record MspHelper(AStar msp, ArrayList<Node> nodes, HashSet<DirectedEdge> edges) {
    }
}