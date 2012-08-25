/*
 * Ordering Tasks
 * 
 * John has n tasks to do. Unfortunately, the tasks are not independent and the execution of one task is only possible 
 * if other tasks have already been executed.
 * 
 * Input begins with a line containing two integers, 1 <= n <= 100 and m. n is the number of tasks (numbered from 1 to n) and 
 * m is the number of direct precedence relations between tasks. After this, there will be m lines with two integers i and j, 
 * representing the fact that task i must be executed before task j.
 *
 * Sample input
 * 5 4
 * 1 2
 * 2 3
 * 1 3
 * 1 5
 * 
 * Sample output
 * 1 4 2 5 3
 */
package orderingtasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Main {

  private BufferedReader in;
  private Crate numbers;
  private String line;
  private ExtendedNode[] graph;
  private List<Node> roots;
  private Queue<Node> result;

  /*
   * Main program.
   */
  public Main () {
    in = new BufferedReader (new InputStreamReader (System.in));
    writeOut ("ORDERING TASKS\n***************\n");
    writeOut ("Input:\n");
    readInput ();
    roots = new ArrayList<Node> ();
    result = new LinkedList<Node> ();
    writeOut ("\nOutput:\n");
    if (topologicalSort ()) {
      for (Iterator<Node> it = result.iterator (); it.hasNext ();) {
        Node node = it.next ();
        System.out.print ((node.getValue () + 1) + " "); // +1 - array starts at 0, so there's need for correction
      }
    }
    else {
      writeOut ("Topological order does not exist.");
    }
  }

  /*
   * Topological sort based on algorithm to test acyclicity.
   * Removes roots from the graph and decreases degrees of nodes. If there is no node left, the topological order is in result 
   * queu.
   * O(|n| + |r|), n = number of tasks, r = number of direct precedence relations between tasks
   */
  private boolean topologicalSort () {
    initializeDegrees (); // count degrees
    findOutRoots (); // find roots
    while (!roots.isEmpty ()) { // while roots not empty
      Node tempNode = roots.get (0); // get one
      int idx = tempNode.getValue ();
      roots.remove (tempNode); // remove it
      tempNode = graph[idx];
      if (tempNode.getNext () != null) { // if it has successors, skip to the first one
        tempNode = tempNode.getNext ();
      }
      else {
        continue; // if it has no successors, continue
      }
      while (tempNode.getNext () != null) { // for every successor of the root decrease degree
        idx = tempNode.getValue ();
        ExtendedNode exNode = (ExtendedNode) graph[idx];
        exNode.decDegree ();
        if (exNode.getDegree () == 0) { // if the node became root, add it to roots and to the topological order
          roots.add (exNode);
          result.add (exNode);
        }
        tempNode = tempNode.getNext ();
      }
      // the same process as above just for the last node of the linked list
      idx = tempNode.getValue ();
      ExtendedNode exNode = (ExtendedNode) graph[idx];
      exNode.decDegree ();
      if (exNode.getDegree () == 0) {
        roots.add (exNode);
        result.add (exNode);
      }
    }
    if (result.size () != graph.length) { // was algorith successful?
      return false;
    }
    return true;
  }

  /*
   * Counts degrees of of all the nodes.
   * O(|n| + |r|), n = number of tasks, r = number of direct precedence relations between tasks
   */
  private void initializeDegrees () {
    for (Node node : graph) {
      if (node.getNext () != null) {
        node = node.getNext ();
      }
      else {
        continue;
      }
      while (node.getNext () != null) { // increase degree for every successor
        int idx = node.getValue ();
        ((ExtendedNode) graph[idx]).incDegree ();
        node = node.getNext ();
      }
      // same as above for the last one
      int idx = node.getValue ();
      ((ExtendedNode) graph[idx]).incDegree ();
    }
  }

  /*
   * Finds out all the roots of the graph.
   * O(|n|), n = number of tasks
   */
  private void findOutRoots () {
    for (ExtendedNode exNode : graph) {
      if (exNode.getDegree () == 0) { // if degree equals zero, it is root
        roots.add (exNode);
        result.add (exNode);
      }
    }
  }

  /*
   * Reads user input and creates a representation of the graph.
   */
  private void readInput () {
    readAndProcessLine ();
    graph = new ExtendedNode[numbers.n1];
    initializeNodeArray (graph);
    int numberOfDependancies = numbers.n2;
    for (int i = 0; i < numberOfDependancies; i++) {
      readAndProcessLine ();
      if (isOutOfBounds (graph.length)) {
        writeOut ("Wrong input!");
        System.exit (-1);
      }
      else {
        addLast (graph[numbers.n1 - 1], numbers.n2 - 1); // -1 - correction so the array starts at 0
      }
    }
  }

  /*
   * Reads a line from standard input, processes it and prepares data for other methods.
   */
  private void readAndProcessLine () {
    try {
      line = in.readLine ();
      numbers = splitLineToNumbers (line);
      if (numbers == null) {
        writeOut ("Wrong input!");
        System.exit (-1);
      }
      if (numbers.n1 < 1) {
        writeOut ("Wrong input!");
        System.exit (-1);
      }
      if (numbers.n2 < 1) {
        writeOut ("Wrong input!");
        System.exit (-1);
      }
    }
    catch (IOException ex) {
      System.out.println ("IOException: Couldn't read from standard input!");
      System.exit (-1);
    }
  }

  /*
   * Splits the given line to two numbers divided by separator and creates a Crate from them. It also check whether they are numbers or not.
   */
  private Crate splitLineToNumbers (String line) {
    Crate crate = null;
    try {
      String[] strArr = line.split ("\\s", 2);
      crate = new Crate (Integer.valueOf (strArr[0]), Integer.valueOf (strArr[1]));
    }
    catch (NumberFormatException ex) {
      writeOut ("NumberFormatException: Wrong input!");
      System.exit (-1);
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      writeOut ("ArrayIndexOutOfBoundsException: Wrong input!");
      System.exit (-1);
    }
    return crate;
  }

  /*
   * Help method to write out to standard output.
   */
  private void writeOut (String msg) {
    System.out.println (msg);
  }

  /*
   * Checks whether the entered number is out of bounds.
   */
  private boolean isOutOfBounds (int length) {
    if (numbers.n1 > length) {
      return true;
    }
    if (numbers.n2 > length) {
      return true;
    }
    return false;
  }

  /*
   * Adds new Node with given value to the end of linked list of the given Node.
   */
  private void addLast (Node node, int n2) {
    while (node.getNext () != null) { // get to the end
      node = node.getNext ();
    }
    Node tempNode = new Node (n2, null);
    node.setNext (tempNode);
  }

  /*
   * Initializes the given array with array.length number of ExtendedNodes.
   */
  private void initializeNodeArray (ExtendedNode[] nodeArray) {
    for (int i = 0; i < nodeArray.length; i++) {
      ExtendedNode tempNode = new ExtendedNode (i, null);
      nodeArray[i] = tempNode;
    }
  }

  /*
   * Help method to write out content of the graph represented by an array of pointers to linked list of successors.
   */
  private void writeOutGraph () {
    for (Node node : graph) {
      while (node.getNext () != null) {
        if (node instanceof ExtendedNode) {
          System.out.print ((node.getValue () + 1) + " {" + ((ExtendedNode) node).getDegree () + "} ");
        }
        else {
          System.out.print ((node.getValue () + 1) + " ");
        }
        node = node.getNext ();
      }
      if (node instanceof ExtendedNode) {
        System.out.println ((node.getValue () + 1) + " {" + ((ExtendedNode) node).getDegree () + "} ");
      }
      else {
        System.out.println ((node.getValue () + 1) + " ");
      }
    }
    System.out.println ("");
    System.out.println ("");
  }

  public static void main (String[] args) {
    new Main ();
  }

  /*
   * Design pattern: Crate
   */
  class Crate {

    public int n1;
    public int n2;

    public Crate (int n1, int n2) {
      this.n1 = n1;
      this.n2 = n2;
    }
  }
}
