package LocationProcessorServer.controller;

import java.util.ArrayList;

import LocationProcessorServer.datastructures.Route;

/**
 * This class globally stores the routes
 * 
 * @author simon_000
 */
public class SystemData {
	/**
	 * Processed routes
	 */
	private static ArrayList<Route> routes;
	/**
	 * Routes abstracted by Spots (Reduced to only significant and necessary
	 * GPS_plus points with the Spots assigned)
	 */
	private static ArrayList<Route> abstractedBySpots;
	/**
	 * Routes abstracted by Nodes (Reduced to only significant and necessary
	 * GPS_plus points with the Nodes assigned)
	 */
	private static ArrayList<Route> abstractedByNodes;

	// --------------------------
	// Getter and Setter methods
	// --------------------------
	public static ArrayList<Route> getRoutes() {
		return routes;
	}

	public static void setRoutes(ArrayList<Route> routes) {
		SystemData.routes = routes;
	}

	public static ArrayList<Route> getAbstractedBySpots() {
		return abstractedBySpots;
	}

	public static void setAbstractedBySpots(ArrayList<Route> abstractedBySpots) {
		SystemData.abstractedBySpots = abstractedBySpots;
	}

	public static ArrayList<Route> getAbstractedByNodes() {
		return abstractedByNodes;
	}

	public static void setAbstractedByNodes(ArrayList<Route> abstractedByNodes) {
		SystemData.abstractedByNodes = abstractedByNodes;
	}
}
