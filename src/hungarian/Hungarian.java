/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hungarian;

import java.util.ArrayList;
import java.util.List;
import utils.ArrUtils;

/**
 * Computes result of the Hungarian Algorithm on two lists of nodes
 * @author brock
 * @param <T>
 * @param <S>
 */
public class Hungarian<T extends Matchable, S extends Matchable> {
    private final ArrayList<Node<T>> leftNodes;
    private final ArrayList<Node<S>> rightNodes;

    /**
     * Prepares the data provided for computing hungarian algorithm.
     * left right are put into a bipartite graph
     * @param left
     * @param right
     */
    public Hungarian(List<T> left, List<S> right) {
        /* add all objects to node list */
        leftNodes = new ArrayList<>();
        for (T i : left) {
            leftNodes.add(new Node<>(i, true));
        }
        rightNodes = new ArrayList<>();
        for (S i : right) {
            rightNodes.add(new Node<>(i, false));
        }

        /* if the lists are of different sizes, even out with null nodes */
        /*if (leftNodes.size() < rightNodes.size()) {
            int diff = rightNodes.size() - leftNodes.size();
            for (int i = 0; i < diff; i ++) {
                leftNodes.add(new Node<>(null, true));
            }
        } else if (rightNodes.size() < leftNodes.size()) {
            int diff = leftNodes.size() - rightNodes.size();
            for (int i = 0; i < diff; i ++) {
                rightNodes.add(new Node<>(null, false));
            }
        }*/

        /* connect all nodes */
        for (Node i : leftNodes) {
            for (Node j: rightNodes) {
                i.addEdge(j);
            }
        }

        //printMatrix();
    }

    /**
     * computes the result
     */
    public void compute() {
        reduce();
        //printMatrix();
        int numMarks = markZeros();

        while (numMarks < leftNodes.size()) {
            unmark();
            numMarks = markZeros();
            markReduce();
            //printMatrix();
        }
        unmark();
        resolveMatches();
    }

    /**
     * uses zero connection to match each node to its optimal partner
     * @return
     */
    private void resolveMatches() {
        /* create dump of all nodes */
        ArrayList<Node> nodes = (ArrayList<Node>)leftNodes.clone();
        nodes.addAll(rightNodes);
        /* sort nodes descending according to number of unmarked zeroes */
        nodes.sort((Node a, Node b) -> a.getNumZeros() - b.getNumZeros());

        int unmatched = leftNodes.size();
        while (unmatched > 0) {
            Node node = nodes.get(0);
            node.findMatch();
            //System.out.println(node + " <-> " + node.getMatch());
            unmatched --;
            nodes.remove(node);
            nodes.remove(node.getMatch());
        }
    }

    /**
     * unmarks all nodes
     */
    private void unmark() {
        for (Node i : leftNodes) {
            i.unmark();
        }
        for (Node i : rightNodes) {
            i.unmark();
        }
    }

    /**
     * reduces each node by its least weighted edge
     */
    private void reduce() {
        for (Node i : leftNodes) {
            Edge leastEdge = i.getLeastEdge();
            i.reduceEdges(leastEdge.getWeight());
        }
        for (Node i : rightNodes) {
            Edge leastEdge = i.getLeastEdge();
            i.reduceEdges(leastEdge.getWeight());
        }
    }

    /**
     * reduces or increases each edge based on mark count
     */
    public void markReduce() {
        /* find least unmarked edge */
        int min = -1;
        for (Node node : leftNodes) {
            for (Edge edge : (List<Edge>)node.getEdges()) {
                if (edge.getMarks() == 0 &&
                        (min == -1 || edge.getWeight() < min)) {
                    min = edge.getWeight();
                }
            }
        }

        /* perform reductions */
        for (Node node : leftNodes) {
            for (Edge edge : (List<Edge>)node.getEdges()) {
                if (edge.getMarks() == 0) {
                    edge.subtractWeight(min);
                } else if (edge.getMarks() == 2) {
                    edge.addWeight(min);
                }
            }
        }
    }

    /**
     * Marks nodes so that all edges with zero weights have
     * a marked node
     */
    private int markZeros() {
        /* create dump of all nodes */
        ArrayList<Node> allNodes = (ArrayList<Node>)leftNodes.clone();
        allNodes.addAll(rightNodes);
        /* sort nodes descending according to number of unmarked zeroes */
        allNodes.sort((Node a, Node b) -> b.getNumZeros() - a.getNumZeros());

        /* keep marking the first node while keeping the list sorted
         * until all zeroes are marked
         */
        int numMarked = 0;
        Node node = allNodes.get(0);
        while (node.getNumZeros() > 0) {
            /* mark the first node and reinsert all affected nodes */
            node.mark();
            numMarked ++;

            allNodes.remove(node);
            ArrUtils.insertSorted(allNodes, node,
                    (Node a, Node b) -> b.getNumZeros() - a.getNumZeros());
            for (Node i : (List<Node>)node.getZeroConnected()) {
                allNodes.remove(i);
                ArrUtils.insertSorted(allNodes, i,
                    (Node a, Node b) -> b.getNumZeros() - a.getNumZeros());
            }

            node = allNodes.get(0);
        }
        /* if the number marked is equal to the number of nodes on left,
         * assignment is possible
         */
        return numMarked;
    }

    private void printMatrix() {
        System.out.print("  ");
        for (Node i : rightNodes) {
            System.out.print(" " + i.getObj());
        }
        System.out.println();
        for (Node i : leftNodes) {
            System.out.print(i.getObj());
            for (Edge j: (List<Edge>)i.getEdges()) {
                System.out.print("  "  + j.getWeight());
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        /*System.out.println("TEST:");
        ArrayList<Seller> left = new ArrayList<>();
        ArrayList<Buyer> right = new ArrayList<>();
        int n = 300;
        for (int i = 0; i < n; i ++) {
            left.add("l" + i);
            right.add("r" + i);
        }
        Hungarian<String, String> h = new Hungarian<>(left, right);
        h.compute();*/
    }
}
