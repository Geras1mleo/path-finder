import opgave.DirectedEdge;
import opgave.Node;
import oplossing.MyPriorityQueue;
import oplossing.MyShortestPath;
import oplossing.SkewHeap;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

import static oplossing.GraphMaker.*;

public class ShortestPathBenchmarks extends Benchmarks {

    public static void main(String[] args) throws NoSuchMethodException {
        new ShortestPathBenchmarks().runBench();
    }

    public void runBench() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("bench", MyShortestPath.class, Node.class, Node.class);
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


    private static MspHelper getMyShortestPath(int nodes, int edges) {
        var msp = new MyShortestPath();
        msp.setPriorityQueueFactory(MyPriorityQueue::new);

        var nodesList = new ArrayList<Node>();
        var edgesSet = new HashSet<DirectedEdge>();

        makeCoherentGraph(nodes, edges, nodesList, edgesSet);

        msp.setGraph(nodesList, edgesSet.stream().toList());
        return new MspHelper(msp, nodesList, edgesSet);
    }

    @RunShortestPath(nodes = 10000, edges = 100)
    @RunShortestPath(nodes = 10000, edges = 1000)
    @RunShortestPath(nodes = 10000, edges = 10000)
    @RunShortestPath(nodes = 10000, edges = 100000)
    @RunShortestPath(nodes = 10000, edges = 1000000)
    private void bench(MyShortestPath msp, Node from, Node to) {
        msp.shortestPath(from, to);
    }
    
/*
SKEW HEAP
    => nodes = 1000000, edges = 2004912
		=> sample size: 10	mean: 1797.051ms	std-dev: 1103.891ms
	=> nodes = 1000000, edges = 2045023
		=> sample size: 10	mean: 2093.18ms	std-dev: 1330.415ms
	=> nodes = 1000000, edges = 2183966
		=> sample size: 10	mean: 2650.666ms	std-dev: 1210.492ms
	=> nodes = 1000000, edges = 10000125
		=> sample size: 10	mean: 5628.485ms	std-dev: 4106.135ms

		
	=> nodes = 10000, edges = 20051
		=> sample size: 10	mean: 28.833ms	std-dev: 19.704ms
	=> nodes = 10000, edges = 20459
		=> sample size: 10	mean: 16.12ms	std-dev: 8.191ms
	=> nodes = 10000, edges = 21965
		=> sample size: 10	mean: 13.783ms	std-dev: 7.531ms
	=> nodes = 10000, edges = 100000
		=> sample size: 10	mean: 15.799ms	std-dev: 16.062ms
	=> nodes = 10000, edges = 1000000
		=> sample size: 10	mean: 9.759ms	std-dev: 8.051ms
		
PAIRING HEAP
    => nodes = 1000000, edges = 2004912
		=> sample size: 10	mean: 1867.183ms	std-dev: 1093.42ms
	=> nodes = 1000000, edges = 2045023
		=> sample size: 10	mean: 1724.113ms	std-dev: 1057.67ms
	=> nodes = 1000000, edges = 2183966
		=> sample size: 10	mean: 2441.686ms	std-dev: 1214.517ms
	=> nodes = 1000000, edges = 10000125
		=> sample size: 10	mean: 4088.545ms	std-dev: 2678.302ms

    
    => nodes = 10000, edges = 20051
		=> sample size: 10	mean: 23.21ms	std-dev: 17.401ms
	=> nodes = 10000, edges = 20459
		=> sample size: 10	mean: 22.14ms	std-dev: 13.285ms
	=> nodes = 10000, edges = 21965
		=> sample size: 10	mean: 11.454ms	std-dev: 7.208ms
	=> nodes = 10000, edges = 100000
		=> sample size: 10	mean: 21.338ms	std-dev: 19.767ms
	=> nodes = 10000, edges = 1000000
		=> sample size: 10	mean: 30.537ms	std-dev: 52.934ms
* */
    
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

    record MspHelper(MyShortestPath msp, ArrayList<Node> nodes, HashSet<DirectedEdge> edges) {
    }
}
/*
Benchmarks op complete grafen heeft weinig zin voor A* 
* */