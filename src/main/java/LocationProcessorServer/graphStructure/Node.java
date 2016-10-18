package LocationProcessorServer.graphStructure;

import java.util.ArrayList;

import LocationProcessorServer.datastructures.Spot;

/**
 * This class defines a Node of the transfer network graph
 * 
 * @author Simon
 *
 */
public class Node {
	/**
	 * ID of the Node
	 */
	int ID;
	
	/**
	 * Counter for the Node IDs
	 */
	static int counter = 0;
	
	/**
	 * Spot that defines the Node
	 */
	Spot spot;
	
	/**
	 * The Edges that connect the Node directly to other Nodes
	 */
	ArrayList<Edge> directEdges;
	
	/**
	 * Nodes that can be reached directly by the attached Edges
	 */
	ArrayList<Integer> reachableNodes;

	/**
	 * Constructor
	 * 
	 * @param spot
	 *            :Spot that defines the Node
	 */
	public Node(Spot spot) {
		this.ID = counter++;
		this.spot = spot;
		directEdges = new ArrayList<Edge>();
		reachableNodes = new ArrayList<Integer>();
	}
}
