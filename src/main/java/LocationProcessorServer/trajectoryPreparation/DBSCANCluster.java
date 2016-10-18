package LocationProcessorServer.trajectoryPreparation;

import java.util.ArrayList;

import LocationProcessorServer.datastructures.GPS_plus;

/**
 * This class is a data-structure for DBSCAN. It represents a cluster of points
 * defined by DBSCAN.
 * 
 * @author simon_000
 *
 */
public class DBSCANCluster {
	/**
	 * GPS_plus point that builds the cluster
	 */
	GPS_plus corepoint;
	/**
	 * List of GPS_plus points in the neighborhood of the core-point
	 */
	ArrayList<GPS_plus> borderPoints;
	/**
	 * Total number of GPS_plus points in the neighborhood
	 */
	int numberOfPts;
	/**
	 * ID of the Cluster
	 */
	int id;
	/**
	 * Counter for the IDs
	 */
	static int counter = 0;

	/**
	 * Constructor
	 * 
	 * @param corepoint:
	 *            GPS_plus object that defines the corepoint
	 */
	public DBSCANCluster(GPS_plus corepoint) {
		super();
		this.corepoint = corepoint;
		this.borderPoints = new ArrayList<GPS_plus>();
		numberOfPts = 1;
		this.id = counter++;
	}

	/**
	 * Adds a GPS_plus point to the neighborhood
	 * 
	 * @param point
	 *            :GPS_plus point to be added
	 */
	public void addPoint(GPS_plus point) {
		borderPoints.add(point);
		numberOfPts = numberOfPts + 1;
	}

	// --------------------------
	// Getter and Setter methods
	// --------------------------

	public GPS_plus getCorepoint() {
		return corepoint;
	}

	public void setCorepoint(GPS_plus corepoint) {
		this.corepoint = corepoint;
	}

	public ArrayList<GPS_plus> getBorderPoints() {
		return borderPoints;
	}

	public void setBorderPoints(ArrayList<GPS_plus> borders) {
		this.borderPoints = borders;
	}

	public int getNoOfPts() {
		return numberOfPts;
	}

	public void setNoOfPts(int noOfPts) {
		this.numberOfPts = noOfPts;
	}
}
