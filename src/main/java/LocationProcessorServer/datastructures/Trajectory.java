package LocationProcessorServer.datastructures;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import entities.*;

/**
 * This class defines a trajectory that is comprised of a sequence of GPS
 * locations
 * 
 * @author simon_000
 */
public class Trajectory {

	/**
	 * Sequence of GPS_plus objects (locations)
	 */
	@JsonProperty
	private ArrayList<GPS_plus> trajectory;
	
	/**
	 * User that created this trajectory
	 */
	@JsonProperty
	private String user;

	/**
	 * Default constructor
	 */
	@JsonCreator
	public Trajectory() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param user
	 *            :user that recorded this trajectory
	 */
	public Trajectory(String user) {
		trajectory = new ArrayList<GPS_plus>();
		this.user = user;
	}

	/**
	 * Constructor
	 * 
	 * @param trajectory
	 *            :sequence of GPS_plus objects
	 * @param userID
	 *            :user that recorded this trajectory
	 */
	Trajectory(ArrayList<GPS_plus> trajectory, String userID) {
		this.trajectory = trajectory;
		this.user = userID;
	}

	/**
	 * Returns the size of the trajectory
	 * 
	 * @return
	 */
	public int size() {
		return trajectory.size();
	}

	/**
	 * Prints the Trajectory
	 */
	public void print() {
		System.out.println("Trajectory from User: ");
		System.out.print(user);
		for (int i = 0; i < this.getTrajectory().size(); i++) {
			System.out.println(getTrajectory().get(i).toString());
		}
	}

	// --------------------------
	// Getter and Setter methods
	// --------------------------

	public ArrayList<GPS_plus> getTrajectory() {
		return trajectory;
	}

	public void setTrajectory(ArrayList<GPS_plus> data) {
		this.trajectory = data;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String userID) {
		this.user = userID;
	}
}
