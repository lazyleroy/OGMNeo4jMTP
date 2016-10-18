package LocationProcessorServer.spotMapping;

import LocationProcessorServer.datastructures.GPS_plus;

/**
 * This class represents a auxiliary class for the spot mapping algorithm
 * 
 * @author simon_000
 *
 */
public class InfoBundle {
	/**
	 * ID of the nearest spot
	 */
	int minDistance_spotID;
	/**
	 * Indicates if the nearest spot is in range
	 */
	boolean inRange;
	/**
	 * Distance to nearest spot
	 */
	double distance;
	/**
	 * Center-Point from the nearest spot
	 */
	GPS_plus minDistance_spotCenter;

	/**
	 * InfoBundle Constructor
	 * 
	 * @param minDistID:
	 *            ID of the nearest spot
	 * @param minDistPoint:
	 *            Center-Point of the nearest spot
	 * @param inRange:
	 *            Indicates if nearest spot is in range
	 * @param distance:
	 *            Distance to nearest spot
	 */
	InfoBundle(int minDistance_spotID, GPS_plus minDistance_spotCenter, boolean inRange, double distance) {
		this.minDistance_spotID = minDistance_spotID;
		this.inRange = inRange;
		this.distance = distance;
		this.minDistance_spotCenter = minDistance_spotCenter;
	}
	
	
	// --------------------------
	// Getter and Setter methods
	// --------------------------
	
	public int getMinDistance_spotID() {
		return minDistance_spotID;
	}

	public void setMinDistance_spotID(int minDistance_spotID) {
		this.minDistance_spotID = minDistance_spotID;
	}
	public boolean isInRange() {
		return inRange;
	}
	public void setInRange(boolean inRange) {
		this.inRange = inRange;
	}
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public GPS_plus getMinDistance_spotCenter() {
		return minDistance_spotCenter;
	}

	public void setMinDistance_spotCenter(GPS_plus minDistance_spotCenter) {
		this.minDistance_spotCenter = minDistance_spotCenter;
	}
}