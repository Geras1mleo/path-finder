import opgave.DirectedEdge;
import opgave.Node;

import java.util.*;

/**
 * Brute force using recursion
 */
public class NaiveShortestPath {
    private static PathResult naiveShortestPath(HashSet<Node> visited, HashMap<Node, ArrayList<DirectedEdge>> neighbors, Node fromNode, Node toNode) {

        if (fromNode.equals(toNode)) {
            return new PathResult(0, new LinkedList<>());
        }

        ArrayList<DirectedEdge> fromNeighbors = neighbors.getOrDefault(fromNode, new ArrayList<>());
        var costs = new ArrayList<PathResult>();
        visited.add(fromNode);

        for (DirectedEdge edge : fromNeighbors) {
            Node nextNode = edge.to();
            if (visited.contains(nextNode)) {
                continue;
            }
            PathResult result = naiveShortestPath(visited, neighbors, nextNode, toNode);
            if (result != null)
                costs.add(result.setCost(result.cost + edge.weight()).addEdge(edge));
        }

        visited.remove(fromNode);

        return costs.stream().min(Comparator.comparing(PathResult::cost)
                .thenComparing(pathResult -> pathResult.edges().size())).orElse(null);
    }

    public static List<DirectedEdge> naiveShortestPath(Collection<DirectedEdge> edges, Node from, Node to) {

        var neighbors = new HashMap<Node, ArrayList<DirectedEdge>>();
        for (var edge : edges) {
            neighbors.putIfAbsent(edge.from(), new ArrayList<>());
            neighbors.get(edge.from()).add(edge);
        }

        PathResult pathResult = naiveShortestPath(new HashSet<>(), neighbors, from, to);
        if (pathResult != null)
            return pathResult.edges();
        return null;
    }

    static final class PathResult {
        private double cost;
        private final List<DirectedEdge> edges;

        PathResult(double cost, LinkedList<DirectedEdge> edges) {
            this.cost = cost;
            this.edges = edges;
        }

        public double cost() {
            return cost;
        }

        public PathResult setCost(double cost) {
            this.cost = cost;
            return this;
        }

        public PathResult addEdge(DirectedEdge e) {
            edges.addFirst(e);
            return this;
        }

        public List<DirectedEdge> edges() {
            return edges;
        }
    }
}
