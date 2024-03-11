package oplossing;

import opgave.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * A* algorithm for MyShortestPath
 * <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">Source</a>
 */
public class MyShortestPath implements RoutePlanner {
    private PriorityQueueFactory queueFactory;
    private HashMap<Node, ArrayList<DirectedEdge>> neighbors;

    @Override
    public void setPriorityQueueFactory(PriorityQueueFactory queueFactory) {
        this.queueFactory = queueFactory;
    }

    @Override
    public void setGraph(List<Node> nodes, List<DirectedEdge> edges) {
        neighbors = new HashMap<>();
        for (var edge : edges) {
            neighbors.putIfAbsent(edge.from(), new ArrayList<>());
            neighbors.get(edge.from()).add(edge);
        }
    }

    @Override
    public List<DirectedEdge> shortestPath(Node from, Node to) {
        PriorityQueue<Double, Node> queue = queueFactory.create();
        HashMap<Node, QueueItem<Double, Node>> queueItems = new HashMap<>();
        queueItems.put(from, queue.add(0.0, from));

        HashMap<Node, Node> previousNodes = new HashMap<>();

        HashMap<Node, Double> weights = new HashMap<>();
        weights.put(from, 0.0);

        while (!queue.isEmpty()) {
            var current = queue.peek();
            Node currentNode = current.getValue();
            if (currentNode.equals(to)) {
                return reconstruct(previousNodes, currentNode);
            }

            queue.poll();
            for (var edge : neighbors.getOrDefault(currentNode, new ArrayList<>())) {
                var neighborNode = edge.to();
                var newWeight = weights.get(currentNode) + edge.weight();
                if (!weights.containsKey(neighborNode) || newWeight < weights.get(neighborNode)) {
                    previousNodes.put(neighborNode, currentNode);
                    weights.put(neighborNode, newWeight);
                    var heuristicWeight = newWeight + distance(neighborNode, to);

                    if (queueItems.containsKey(neighborNode)) {
                        var queueItem = queueItems.get(neighborNode);
                        if (heuristicWeight < queueItem.getPriority()) {
                            queueItem.decreaseKey(heuristicWeight);
                        }
                    } else {
                        queueItems.put(neighborNode, queue.add(heuristicWeight, neighborNode));
                    }
                }
            }
        }

        return null;
    }

    private List<DirectedEdge> reconstruct(HashMap<Node, Node> previousNodes, Node current) {
        var path = new LinkedList<DirectedEdge>();
        var previous = current;
        current = previousNodes.get(current);
        if(current == null)
            return path;
        path.add(getEdge(current, previous));
        while (previousNodes.containsKey(current)) {
            previous = current;
            current = previousNodes.get(current);
            path.addFirst(getEdge(current, previous));
        }
        return path;
    }

    private DirectedEdge getEdge(Node from, Node to) {
        return neighbors.get(from).stream().filter(directedEdge -> directedEdge.to().equals(to)).findFirst().get();
    }

    private double distance(Node from, Node to) {
        return Math.sqrt(Math.pow(from.x() - to.x(), 2) + Math.pow(from.y() - to.y(), 2));
    }
}
