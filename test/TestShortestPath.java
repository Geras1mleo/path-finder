import opgave.DirectedEdge;
import opgave.Node;
import oplossing.AStar;
import oplossing.SkewHeap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static oplossing.GraphMaker.*;

public class TestShortestPath {
    private final Random RG = new Random(69);    

    private static AStar getMyShortestPath() {
        var msp = new AStar();
        msp.setPriorityQueueFactory(SkewHeap::new);
        return msp;
    }

    private static void assertEqualPath(List<DirectedEdge> expected, List<DirectedEdge> actual) {
        if (expected == null)
            assertNull(actual);
        else
            assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void testSimpleGraph() {
        // Arrange:
        var msp = getMyShortestPath();
        Node a = new Node(2, 4);
        Node b = new Node(4, 3);
        Node c = new Node(1, 2);
        DirectedEdge ab = new DirectedEdge(a, b, 1);
        DirectedEdge bc = new DirectedEdge(b, c, 2);
        DirectedEdge ac = new DirectedEdge(a, c, 5);

        // Act:
        List<DirectedEdge> edges = List.of(ab, bc, ac);
        msp.setGraph(List.of(a, b, c), edges);

        // Assert
        var actual = msp.shortestPath(a, c).toArray();
        var expected = NaiveShortestPath.naiveShortestPath(edges, a, c).toArray();
        assertArrayEquals(actual, List.of(ab, bc).toArray());
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testSmallCompleteGraph() {
        // Arrange:
        var elementsCount = 10;

        var msp = getMyShortestPath();
        var nodes = new ArrayList<Node>();
        var edges = new HashSet<DirectedEdge>();

        // Act:
        makeCompleteGraph(elementsCount, nodes, edges);
        msp.setGraph(nodes, edges.stream().toList());

        // Assert:
        var from = nodes.get(RG.nextInt(nodes.size()));
        var to = nodes.get(RG.nextInt(nodes.size()));
        var expected = NaiveShortestPath.naiveShortestPath(edges, from, to).toArray();
        var actual = msp.shortestPath(from, to).toArray();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void testRandomSmallGraph() {
        // Arrange:
        var elementsCount = 10;
        var edgesCount = 50;

        var msp = getMyShortestPath();
        var nodes = new ArrayList<Node>();
        var edges = new HashSet<DirectedEdge>();

        // Act:
        makeCoherentGraph(elementsCount, edgesCount, nodes, edges);
        msp.setGraph(nodes, edges.stream().toList());

        // Assert:
        var from = nodes.get(RG.nextInt(nodes.size()));
        var to = nodes.get(RG.nextInt(nodes.size()));
        var expected = NaiveShortestPath.naiveShortestPath(edges, from, to);
        var actual = msp.shortestPath(from, to);
        assertEqualPath(expected, actual);
    }

    @Test
    public void testRandomBigGraph() {
        // Arrange:
        var elementsCount = 20;
        var edgesCount = 100;

        var msp = getMyShortestPath();
        var nodes = new ArrayList<Node>();
        var edges = new HashSet<DirectedEdge>();

        // Act:
        makeCoherentGraph(elementsCount, edgesCount, nodes, edges);
        msp.setGraph(nodes, edges.stream().toList());

        // Assert:
        var from = nodes.get(RG.nextInt(nodes.size()));
        var to = nodes.get(RG.nextInt(nodes.size()));
        var expected = NaiveShortestPath.naiveShortestPath(edges, from, to);
        var actual = msp.shortestPath(from, to);
        assertEqualPath(expected, actual);
    }
}
