package LocationProcessorServer.spotMapping;

import entities.*;
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
	long minDistance_spotID;
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
	double minDistance_spotCenterlat;
	double minDistance_spotCenterlong;

	/**
	 * InfoBundle Constructor
	 * 
	 * @param minDistance_spotID:
	 *            ID of the nearest spot
	 * @param minDistance_spotID:
	 *            Center-Point of the nearest spot
	 * @param inRange:
	 *            Indicates if nearest spot is in range
	 * @param distance:
	 *            Distance to nearest spot
	 */
	InfoBundle(long minDistance_spotID, double minDistance_spotCenterlat, double minDistance_spotCenterlong, boolean inRange, double distance) {
		this.minDistance_spotID = minDistance_spotID;
		this.inRange = inRange;
		this.distance = distance;
		this.minDistance_spotCenterlat = minDistance_spotCenterlat;
		this.minDistance_spotCenterlong = minDistance_spotCenterlong;
	}
	
	
	// --------------------------
	// Getter and Setter methods
	// --------------------------
	
	public long getMinDistance_spotID() {
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
}