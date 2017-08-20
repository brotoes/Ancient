/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hungarian;

/**
 *
 * @author brock
 */
public class Edge {
    private final Node left;
    private final Node right;
    private int weight;

    public Edge(Node left, Node right, int w) {
        this.left = left;
        this.right = right;
        this.weight = w;
    }

    /**
     * returns true if left or right == node
     * @param node
     * @return
     */
    public boolean contains(Node node) {
        return (left == node || right == node);
    }

    /* getters and setters */
    public Node getLeft() { return left; }
    public Node getRight() { return right; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    public void subtractWeight(int diff) { this.weight -= diff; }
    public void addWeight(int add) { this.weight += add; }
    public int getMarks() {
        int marks = 0;
        if (left.isMarked()) {
            marks ++;
        }
        if (right.isMarked()) {
            marks ++;
        }
        return marks;
    }
}
