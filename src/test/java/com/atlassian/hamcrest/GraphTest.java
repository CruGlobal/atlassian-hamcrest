package com.atlassian.hamcrest;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.atlassian.hamcrest.DeepIsEqual.deeplyEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

/**
 * @author Matt Drees
 */
public class GraphTest {

    @Test
    public void assertThatSmallGraphIsDescribable()
    {
        Graph graph1 = makeSmallGraph("A");
        Matcher<? super Graph> matcher = deeplyEqualTo(graph1);
        StringDescription description = new StringDescription();
        matcher.describeTo(description);
        assertThat(description.toString(), allOf(
            startsWith("{" + System.getProperty("line.separator") + "  nodes ["),
            containsString("value is <"),
            containsString("neighbors ["),
            containsString("&Node<A-1>")
        ));
        System.err.println(description);
    }

    @Test
    public void assertThatSmallIdenticalGraphsMatch()
    {
        Graph graph1 = makeSmallGraph("A");
        Graph graph2 = makeSmallGraph("B");
        assertThat(graph1, is(deeplyEqualTo(graph2)));
    }

    @Test
    public void assertThatMediumIdenticalGraphsMatch()
    {
        Graph graph1 = makeMediumGraph("A");
        Graph graph2 = makeMediumGraph("B");
        //currently takes about 30 seconds
        assertThat(graph1, is(deeplyEqualTo(graph2)));
    }

    private Graph makeSmallGraph(String label) {
        return new GraphMaker().makeGraph(3, .2, .5, label);
    }

    private Graph makeMediumGraph(String label) {
        return new GraphMaker().makeGraph(12, .2, .5, label);
    }


    class GraphMaker {

        Random random;
        private List<Node> nodes;
        private int nodeCount;
        private String label;

        private Graph makeGraph(int nodeCount, double edgeDensityLower, double edgeDensityUpper, String label) {
            this.nodeCount = nodeCount;
            this.label = label;
            random = new Random(0);
            nodes = initializeNodes();
            populateNeighbors(edgeDensityLower, edgeDensityUpper);
            Graph graph = new Graph();
            graph.nodes = Sets.newHashSet(nodes);
            graph.label = label;
            return graph;
        }

        private List<Node> initializeNodes() {
            List<Node> nodes = Lists.newArrayListWithCapacity(nodeCount);

            for (int i = 0; i < nodeCount; i++) {
                Node node = new Node();
                node.value = i;
                node.label = label;
                nodes.add(i, node);
            }
            return nodes;
        }

        private void populateNeighbors(double edgeDensityLower, double edgeDensityUpper) {
            for (Node node : nodes) {
                int edgeCount = determineEdgeCount(edgeDensityLower, edgeDensityUpper);
                node.neighbors = Sets.newHashSetWithExpectedSize(edgeCount);

                while (node.neighbors.size() < edgeCount) {
                    Node neighbor = nodes.get(random.nextInt(nodeCount));
                    node.neighbors.add(neighbor);
                }
            }
        }

        private int determineEdgeCount(double edgeDensityLower, double edgeDensityUpper) {
            double range = edgeDensityLower + edgeDensityUpper;
            double edgeDensity = edgeDensityLower + range * random.nextDouble();
            return (int) (edgeDensity * nodeCount);
        }
    }

    static class Graph {
        Set<Node> nodes;

        //deepEquals() will ignore this
        transient String label;

        @Override
        public String toString() {
            return
                nodes.size() + "-node " +
                countEdges(nodes) + "-edge " +
                "Graph " + label;
        }

        private int countEdges(Set<Node> nodes) {
            int count = 0;
            for (Node node : nodes) {
                count += node.neighbors.size();
            }
            return count;
        }
    }

    static class Node {
        int value;
        Set<Node> neighbors;

        //see note on Graph#label
        transient String label;

        @Override
        public String toString() {
            return "Node<" + label + "-" + value +">";
        }
    }


}
