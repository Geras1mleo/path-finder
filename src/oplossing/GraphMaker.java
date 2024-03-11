package oplossing;

import opgave.DirectedEdge;
import opgave.Node;

import java.util.*;

public class GraphMaker {

    private static final Random RG = new Random(69);

    public static void makeCompleteGraph(int elementsCount, ArrayList<Node> nodes, HashSet<DirectedEdge> edges) {
        for (int i = 0; i < elementsCount; i++) {
            nodes.add(new Node(RG.nextInt(elementsCount), RG.nextInt(elementsCount)));
        }

        // n(n-1) edges in set
        for (Node from : nodes) {
            for (Node to : nodes) {
                if (from.equals(to))
                    continue;
                // Het gewicht is altijd minstens de euclidische afstand tussen de toppen
                var dist = distance(from, to);
                edges.add(new DirectedEdge(from, to, RG.nextInt((int) dist, (int) (dist * 2))));
            }
        }
    }

    public static void makeRandomGraph(int elementsCount, int edgesCount, ArrayList<Node> nodes, HashSet<DirectedEdge> edges) {
        for (int i = 0; i < elementsCount; i++) {
            nodes.add(new Node(RG.nextInt(elementsCount), RG.nextInt(elementsCount)));
        }

        for (int i = 0; i < edgesCount; i++) {
            Node from = nodes.get(RG.nextInt(nodes.size()));
            Node to = null;
            while (to == null) {
                to = nodes.get(RG.nextInt(nodes.size()));
                if (from.equals(to)) {
                    to = null;
                }
            }
            // Het gewicht is altijd minstens de euclidische afstand tussen de toppen
            var dist = distance(from, to);
            edges.add(new DirectedEdge(from, to, RG.nextInt((int) dist, (int) (dist * 2))));
        }
    }

    public static void makeCoherentGraph(int elementsCount, int edgesCount, ArrayList<Node> nodes, HashSet<DirectedEdge> edges) {
        makeRandomGraph(elementsCount, edgesCount, nodes, edges);

        var neighborsLists = new HashMap<Node, ArrayList<DirectedEdge>>();
        for (var edge : edges) {
            neighborsLists.putIfAbsent(edge.from(), new ArrayList<>());
            neighborsLists.get(edge.from()).add(edge);
        }

        var subGraphRoots = new ArrayList<Node>();
        var subGraphLeaves = new ArrayList<HashSet<Node>>();

        var visited = new HashSet<Node>();
        for (Node node : nodes) {
            if (visited.contains(node))
                continue;

            visited.add(node);

            var nextNodes = new Stack<Node>();
            nextNodes.add(node);

            // Depth first search
            var leaves = new HashSet<Node>();
            while (!nextNodes.isEmpty()) {
                Node current = nextNodes.pop();
                if (current != node && visited.contains(current))
                    continue;

                visited.add(current);
                // Add all neighbors to stack to add them later to sub-graph
                List<Node> neighbors = neighborsLists.get(current).stream().map(DirectedEdge::to).toList();
                if (neighbors.isEmpty())
                    leaves.add(current);
                else
                    nextNodes.addAll(neighbors);
            }

            // if Root == leave:
            if (leaves.size() == 1 && leaves.contains(node)) {
                leaves.clear();
            }

            subGraphRoots.add(node);
            subGraphLeaves.add(leaves);
        }

        for (int i = 0; i < subGraphRoots.size(); i++) {
            if (i != 0) {
                var from = subGraphRoots.get(i - 1);
                var to = subGraphRoots.get(i);
                var dist = distance(from, to);
                // Add bidirectional
                edges.add(new DirectedEdge(from, to, RG.nextInt((int) dist, (int) (dist * 2))));
                edges.add(new DirectedEdge(to, from, RG.nextInt((int) dist, (int) (dist * 2))));
            }

            for (var leave : subGraphLeaves.get(i)) {
                var dist = distance(leave, subGraphRoots.get(i));
                edges.add(new DirectedEdge(leave, subGraphRoots.get(i), RG.nextInt((int) dist, (int) (dist * 2))));
            }
        }
    }

    private static double distance(Node from, Node to) {
        return Math.sqrt(Math.pow(from.x() - to.x(), 2) + Math.pow(from.y() - to.y(), 2));
    }
}
