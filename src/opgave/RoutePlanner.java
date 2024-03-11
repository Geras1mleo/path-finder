package opgave;

import java.util.List;

/**
 * Interface for a route planner on a directed graph with adjustable priority queue.
 */
public interface RoutePlanner {

    /**
     * Sets the directed graph to be used by the route planner.
     *
     * @param nodes list of nodes
     * @param edges list of directed edges
     */
    void setGraph(List<Node> nodes, List<DirectedEdge> edges);

    /**
     * Sets a factory for the priority queue to be used by the route planner.
     *
     * @param queueFactory a factory which provides an empty priority queue with decrease-key functionality
     */
    void setPriorityQueueFactory(PriorityQueueFactory queueFactory);

    /**
     * Returns the shortest path from the node with id "from" to the node with id "to" using edge weights as priorities.
     * Requires that the graph and priority queue have been set.
     *
     * @param from node where the path should start
     * @param to node where the path should end
     * @return a list of nodes on the shortest path from "from" to "to" (both included)
     */
    List<DirectedEdge> shortestPath(Node from, Node to);

}
