package orderingtasks;

/*
 * Class representing a node in a graph extended with a degree value necessary for topological sort.
 */
public class ExtendedNode extends Node {

  private int degree = 0;

  public ExtendedNode (int value, Node next) {
    super (value, next);
  }

  public int getDegree () {
    return degree;
  }

  public void incDegree () {
    degree++;
  }

  public void decDegree () {
    degree--;
  }
}
