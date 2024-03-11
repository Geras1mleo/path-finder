package opgave;

/**
 * Represents a directed edge in a graph.
 *
 * @param from the node where the edge starts
 * @param to the node where the edge ends
 * @param weight the weight of this edge
 */
public record DirectedEdge(Node from, Node to, double weight) {}
