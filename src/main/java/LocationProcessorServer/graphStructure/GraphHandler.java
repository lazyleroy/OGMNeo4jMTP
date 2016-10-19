package LocationProcessorServer.graphStructure;

import java.util.ArrayList;

import LocationProcessorServer.datastructures.GPS_plus;
import LocationProcessorServer.datastructures.Route;
import entities.*;

/**
 * This class maintains the Nodes and Edges of the transfer network graph and
 * updates them if new Nodes of Edges occur
 * 
 * @author Simon
 *
 */
public class GraphHandler {

	/**
	 * Nodes of the transfer network graph
	 */
	static ArrayList<Node> nodes = new ArrayList<Node>();
	/**
	 * Edges of the transfer network graph
	 */
	static ArrayList<Edge> edges = new ArrayList<Edge>();

	/**
	 * Updates the graph
	 * 
	 * Notice: Modified compared to the description in the written report of the
	 * thesis
	 *
	 * 
	 * @param route
	 *            :route that was already mapped into spots
	 */
	public static void updateGraph(Route route) {
		// check if new spots occurred and delete the edge
		// they was involved in
		ArrayList<GPS_plus> trajectory = route.getTrajectory();
		Spot lastSpot = null;
		for (int i = 0; i < trajectory.size(); i++) {
			Spot spot = trajectory.get(i).getSpot();
			if (spot != lastSpot) {
				if (spot != null) {
					if (spot.isIntersection() || spot.getNeighbors().size() == 1) {
						if (spot.node == null) {
							deleteEdge(spot);
							createNode(spot);
						}
					}
				}
			}
			lastSpot = spot;
		}
		// check if some former nodes doesn't fulfill the node condition any
		// more
		// if yes, delete the node and its outgoing edges
		lastSpot = null;
		for (int i = 0; i < trajectory.size(); i++) {
			Spot spot = trajectory.get(i).getSpot();
			if (spot != lastSpot) {
				if (spot != null) {
					if (spot.getNeighbors().size() == 2) {
						if (spot.node != null) {
							deleteEdge(spot);
							for (int k = 0; k < nodes.size(); k++) {
								if (spot.node.ID == nodes.get(k).ID) {
									nodes.remove(k);
								}
							}
							spot.node = null;
						}
					}
				}
			}
			lastSpot = spot;
		}
		// search for all edges of all nodes that this trajectory crosses
		lastSpot = null;
		for (int i = 0; i < trajectory.size(); i++) {
			Spot spot = trajectory.get(i).getSpot();
			if (spot != lastSpot) {
				if (spot != null) {
					if (spot.node != null) {
						searchEdges(spot);
					}
				}
			}
			lastSpot = spot;
		}
		lastSpot = null;
	}

	/**
	 * Method to delete the Edge, in which the input spot is involved.
	 * 
	 * @param spot
	 *            :The spot that will be searched for in the Edges, to delete
	 *            the Edge this Spot is involved to
	 */
	static void deleteEdge(Spot spot) {
		long IDtoDelete = spot.getSpotID();
		boolean contained = false;
		for (int i = 0; i < edges.size(); i++) {
			ArrayList<Spot> segments = edges.get(i).spotSegments;
			for (int j = 0; j < segments.size(); j++) {
				if (segments.get(j).getSpotID() == IDtoDelete) {
					contained = true;
				}
			}
			if (contained) {
				for (int j = 0; j < segments.size(); j++) {
					segments.get(j).setEdgeProcessed(false);
				}
				Edge edge = edges.remove(i);
				// Node1
				ArrayList<Edge> n1edges = edge.node1.directEdges;
				for (int j = 0; j < n1edges.size(); j++) {
					if (n1edges.get(j).ID == edge.ID) {
						n1edges.remove(j);
					}
				}
				ArrayList<Integer> n1nodes = edge.node1.reachableNodes;
				for (int j = 0; j < n1nodes.size(); j++) {
					if (n1nodes.get(j) == edge.node2.ID) {
						n1nodes.remove(j);
					}
				}
				// Node2
				ArrayList<Edge> n2edges = edge.node2.directEdges;
				for (int j = 0; j < n2edges.size(); j++) {
					if (n2edges.get(j).ID == edge.ID) {
						n2edges.remove(j);
					}
				}
				ArrayList<Integer> n2nodes = edge.node2.reachableNodes;
				for (int j = 0; j < n2nodes.size(); j++) {
					if (n2nodes.get(j) == edge.node1.ID) {
						n2nodes.remove(j);
					}
				}
				// i = edges.size();
			}
		}
	}

	/**
	 * Creates a new Node
	 * 
	 * @param spot
	 *            :Spot that defines the Node
	 */
	static void createNode(Spot spot) {
		Node node = new Node(spot);
		spot.node = node;
		nodes.add(node);
	}

	/**
	 * Searches for all Edges of a Node
	 * 
	 * @param spot
	 *            :Spot that defines the Node
	 */
	static void searchEdges(Spot spot) {
		Node start = spot.node;
		spot.setEdgeProcessed(true);
		for (int i = 0; i < spot.getNeighbors().size(); i++) {
			if (!spot.getNeighbors().get(i).isEdgeProcessed()) {
				ArrayList<Spot> edgeSegment = new ArrayList<Spot>();
				edgeSegment.add(spot);
				getEdgeSegment(start, spot.getNeighbors().get(i), edgeSegment);
			}
		}
		spot.setEdgeProcessed(false);
	}

	/**
	 * Method that is used iteratively within searching Edges
	 * 
	 * @param startNode
	 *            :Node that started to search for Edges
	 * @param spot
	 *            :Current spot
	 * @param segments
	 *            :Current spot segment
	 */
	static void getEdgeSegment(Node startNode, Spot spot, ArrayList<Spot> segments) {
		if (spot.node == null) {
			spot.setEdgeProcessed(true);
			segments.add(spot);
			for (int i = 0; i < spot.getNeighbors().size(); i++) {
				if (!spot.getNeighbors().get(i).isEdgeProcessed()) {
					getEdgeSegment(startNode, spot.getNeighbors().get(i), segments);
				}
			}
		} else {
			spot.setEdgeProcessed(true);
			segments.add(spot);
			createEdge(startNode, spot.node, segments);
		}
	}

	/**
	 * Creates an Edge
	 * 
	 * @param start
	 *            :Node 1
	 * @param end
	 *            :Node 2
	 * @param segments
	 *            :Spot segment that builds the Edge
	 */
	static void createEdge(Node start, Node end, ArrayList<Spot> segments) {
		String semantic = start.ID + "_" + end.ID;
		String semanticInv = end.ID + "_" + start.ID;
		boolean contained = false;
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).semanticID.equals(semantic) || edges.get(i).semanticID.equals(semanticInv)) {
				contained = true;
			}
		}
		if (!contained) {
			Edge newEdge = new Edge(start, end, segments);
			edges.add(newEdge);
			start.directEdges.add(newEdge);
			end.directEdges.add(newEdge);
			start.reachableNodes.add(end.ID);
			end.reachableNodes.add(start.ID);
		}
	}

	/**
	 * Deletes an Node and merges it two Edges 
	 * !! unused !!
	 * 
	 * @param spot
	 *            :Spot that defines the Node
	 */
	static void deleteNode(Spot spot) {
		Node n = spot.node;
		for (int i = 0; i < nodes.size(); i++) {
			if (n.ID == nodes.get(i).ID) {
				nodes.remove(i);
			}
		}
		ArrayList<Edge> directEdges = n.directEdges;
		for (int i = 0; i < edges.size();) {
			if (edges.get(i).ID == directEdges.get(0).ID) {
				edges.remove(i);
			} else if (edges.get(i).ID == directEdges.get(1).ID) {
				edges.remove(i);
			} else {
				i++;
			}
		}

		ArrayList<Spot> segments = new ArrayList<Spot>();
		segments.addAll(directEdges.get(0).spotSegments);
		spot.setEdgeProcessed(true);

		Node n1;
		if (directEdges.get(0).node1.ID == n.ID) {
			n1 = directEdges.get(0).node2;
		} else {
			n1 = directEdges.get(0).node1;
		}

		segments.add(spot);

		Node n2;
		segments.addAll(directEdges.get(1).spotSegments);
		if (directEdges.get(1).node1.ID == n.ID) {
			n2 = directEdges.get(1).node2;
		} else {
			n2 = directEdges.get(1).node1;
		}
		Edge newEdge = new Edge(n1, n2, segments);
		edges.add(newEdge);
		n1.directEdges.add(newEdge);
		n2.directEdges.add(newEdge);
		ArrayList<Integer> n1nodes = n1.reachableNodes;
		for (int j = 0; j < n1nodes.size(); j++) {
			if (n1nodes.get(j) == n.ID) {
				n1nodes.remove(j);
			}
		}
		ArrayList<Integer> n2nodes = n2.reachableNodes;
		for (int j = 0; j < n2nodes.size(); j++) {
			if (n2nodes.get(j) == n.ID) {
				n2nodes.remove(j);
			}
		}
		spot.node = null;
	}

	/**
	 * Prints the Nodes and Edges of the Graph
	 */
	public static void printGraphResult() {
		System.out.println("Nodes:");
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println(nodes.get(i).spot.getSpotID());
		}
		System.out.println("Edges:");
		for (int i = 0; i < edges.size(); i++) {
			System.out.println(edges.get(i).node1.spot.getSpotID());
			System.out.println(edges.get(i).node2.spot.getSpotID());
			System.out.println("--------------------");
		}
	}
}
