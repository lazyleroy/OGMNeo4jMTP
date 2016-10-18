package LocationProcessorServer.graphStructure;

import java.util.ArrayList;

import LocationProcessorServer.datastructures.Spot;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;

/**
 * This class defines an Edge of the transfer network graph
 * 
 * @author Simon
 *
 */
public class Edge {
	/**
	 * Unique Edge-ID
	 */
	int ID;

	/**
	 * Counter for the Edge IDs
	 */
	static int counter = 0;

	/**
	 * Unique semantic Edge-ID Built as follows: Node1.ID_Node2.ID where Node1
	 * and Node2 are the connected Nodes.
	 */
	String semanticID;

	/**
	 * First connected Node
	 */
	Node node1;

	/**
	 * Second connected Node
	 */
	Node node2;

	/**
	 * Sequence of Spots that connect the two Nodes and built the Edge
	 */
	ArrayList<Spot> spotSegments;

	/**
	 * Weight of the edge: calculated out of the distance of the edge
	 */
	double weight;

	/**
	 * Constructor
	 * 
	 * @param node1
	 *            :First connected node
	 * @param node2
	 *            :Second connected node
	 * @param segment
	 *            :Sequence of Spots that connect the two Nodes and built the
	 *            Edge
	 */
	public Edge(Node node1, Node node2, ArrayList<Spot> segment) {
		this.ID = counter++;
		this.node1 = node1;
		this.node2 = node2;
		this.spotSegments = segment;
		this.semanticID = node1.ID + "_" + node2.ID;
		// calculate weight
		this.weight = calcWeight(segment);
	}

	/**
	 * Calculates the weight of the edge (includes the distance of the first and
	 * last spots in the segments to the respective nodes)
	 * 
	 * @param segment
	 *            :spotSegment of the Edge
	 * @return weight as double
	 */
	private static double calcWeight(ArrayList<Spot> segment) {
		double weight = 0.0;
		if (segment.size() > 0) {
			Spot s1 = segment.get(0);
			s1.weightProcessed = true;
			ArrayList<Spot> n = s1.getNeighbors();
			for (int j = 0; j < n.size(); j++) {
				if (!n.get(j).weightProcessed) {
					calculateWeight(n.get(j), s1, weight);
				}
			}
			for (int i = 0; i < segment.size(); i++) {
				segment.get(i).weightProcessed = false;
			}
		} else {
		}
		return weight;
	}

	/**
	 * Helps to calculate the weight of the edge (recursively)
	 * 
	 * @param now
	 *            :current spot
	 * @param before
	 *            :neighbored spot that led to the current spot
	 * @param value
	 *            :current value of the weight
	 */
	private static void calculateWeight(Spot now, Spot before, double value) {
		if (now.weightProcessed == false) {
			now.weightProcessed = true;
			value = value + GPSDataProcessor.calcDistance(now.getSpotCenter(), before.getSpotCenter());
		}
		if (!now.isIntersection()) {
			ArrayList<Spot> n = now.getNeighbors();
			for (int j = 0; j < n.size(); j++) {
				if (!n.get(j).weightProcessed) {
					calculateWeight(n.get(j), now, value);
				}
			}
		}
	}
}
