package LocationProcessorServer.datastructures;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class defines a route that is comprised of a sequence of GPS locations
 * and got a unique identifier
 * 
 * @author simon_000
 */
public class Route extends Trajectory {

	/**
	 * Counter for the Route IDs
	 */
	private static int counter = 0;

	/**
	 * Route-ID
	 */
	@JsonProperty
	private int routeID;

	/**
	 * True if the route was already mapped into spots
	 */
	@JsonProperty
	private boolean spotProcessed;

	/**
	 * Default constructor
	 */
	@JsonCreator
	public Route() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param route
	 *            :route-trajectory (sequence of GPS_plus objects)
	 * @param user
	 *            :user that recorded this route
	 */
	public Route(ArrayList<GPS_plus> route, String user) {
		super(route, user);
		this.routeID = counter++;
	}

	/**
	 * Prints the Route
	 */
	@Override
	public void print() {
		System.out.println("Route with ID: " + routeID);
		System.out.println("Related user: " + getUser());
		System.out.println("Route-Data: ");
		for (int i = 0; i < this.getTrajectory().size(); i++) {
			System.out.println(getTrajectory().get(i).toString());
		}
	}

	// --------------------------
	// Getter and Setter methods
	// --------------------------

	public int getRouteID() {
		return routeID;
	}

	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}

	public boolean isSpotProcessed() {
		return spotProcessed;
	}

	public void setSpotProcessed(boolean spotProcessed) {
		this.spotProcessed = spotProcessed;
	}
}
