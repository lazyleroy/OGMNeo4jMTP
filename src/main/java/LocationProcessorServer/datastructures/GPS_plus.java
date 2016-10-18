package LocationProcessorServer.datastructures;

import java.util.ArrayList;
import java.util.Date;

import LocationProcessorServer.spotMapping.InfoBundle;
import LocationProcessorServer.trajectoryPreparation.DBSCANCluster;

/**
 * This class defines a single GPS location
 * 
 * @author simon_000
 *
 */
public class GPS_plus {
	/**
	 * Data identifier
	 */
	private int dataID;
	private static int counter = 0;
	/**
	 * Information about latitude
	 */
	private float latitude;

	/**
	 * Information about longitude
	 */
	private float longitude;
	/**
	 * Timestamp of GPS-Data
	 */
	private Date time;
	/**
	 * Username
	 */
	private String userID;
	/**
	 * ID of the assigned route
	 */
	private int routeID;
	/**
	 * information about heading information
	 */
	private double head;
	/**
	 * information about speed in M/S
	 */
	private double speed;
	/**
	 * distance to the next point in trajectory
	 */
	private double distanceToNextPoint;
	/**
	 * time difference to the next point in trajectory
	 */
	private long timediffToNextPoint;
	/**
	 * id of the next gps point in trajectory
	 */
	private int leadsTo;
	// -----------------------------------------------------------
	// Variables for abstraction with spots
	/**
	 * true if GPS-point was already mapped into a spot
	 */
	private boolean mappedToSpot;
	/**
	 * information about assigned information
	 */
	private Spot spot;
	/**
	 * information about nearest spot
	 */
	private InfoBundle closestSpotInfo;

	// -----------------------------------------------------------
	// Variables for DBSCAN
	/**
	 * information in DBSCAN phase to delete stay points; values: corepoint,
	 * border, null
	 */
	private String pointInfoDBSAN;
	/**
	 * neighbors within a specific distance
	 */
	private ArrayList<GPS_plus> neighborsDBSCAN;
	/**
	 * true if GPS-point was already processed while DBSCAN-clustering
	 */
	private boolean processedDBSCAN;
	/**
	 * assigned DBSCAN-cluster
	 */
	private DBSCANCluster clusterDBSCAN;

	// -----------------------------------------------------------
	// Constructor
	/**
	 * Constructor
	 * 
	 * @param latitude
	 *            :float
	 * @param longitude
	 *            :float
	 * @param time
	 *            :Date
	 * @param userID
	 *            :String
	 */
	public GPS_plus(float latitude, float longitude, Date time, String userID) {
		super();
		this.dataID = GPS_plus.counter++;
		this.latitude = latitude;
		this.longitude = longitude;
		this.time = time;
		this.userID = userID;
	}

	/**
	 * Default Constructor
	 */
	public GPS_plus() {
		super();
		this.dataID = GPS_plus.counter++;
		this.latitude = 0.0f;
		this.longitude = 0.0f;
		this.time = null;
		this.userID = null;
	}

	// -----------------------------------------------------------
	// Getter, Setter & more
	public String toString() {
		return "Data-ID: " + this.dataID + " // Latitude: " + this.latitude + " // Longitude: " + this.longitude
				+ " // Time: " + this.time;
	}

	public ArrayList<GPS_plus> getNeighborsDBSCAN() {
		return neighborsDBSCAN;
	}

	public void setNeighborsDBSCAN(ArrayList<GPS_plus> neighborsDBSCAN) {
		this.neighborsDBSCAN = neighborsDBSCAN;
	}

	public boolean isProcessedDBSCAN() {
		return processedDBSCAN;
	}

	public void setProcessedDBSCAN(boolean processedDBSCAN) {
		this.processedDBSCAN = processedDBSCAN;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getPointInfoDBSAN() {
		return pointInfoDBSAN;
	}

	public void setPointInfoDBSAN(String pointInfoDBSAN) {
		this.pointInfoDBSAN = pointInfoDBSAN;
	}

	public DBSCANCluster getClusterDBSCAN() {
		return clusterDBSCAN;
	}

	public void setClusterDBSCAN(DBSCANCluster clusterDBSCAN) {
		this.clusterDBSCAN = clusterDBSCAN;
	}

	public int getDataID() {
		return dataID;
	}

	public void setDataID(int dataID) {
		this.dataID = dataID;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public int getRouteID() {
		return routeID;
	}

	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Spot getSpot() {
		return spot;
	}

	public void setSpot(Spot spot) {
		this.spot = spot;
	}

	public double getHead() {
		return head;
	}

	public void setHead(double head) {
		this.head = head;
	}

	public InfoBundle getClosestSpotInfo() {
		return closestSpotInfo;
	}

	public void setClosestSpotInfo(InfoBundle closestSpotInfo) {
		this.closestSpotInfo = closestSpotInfo;
	}

	public boolean isMappedToSpot() {
		return mappedToSpot;
	}

	public void setMappedToSpot(boolean mappedToSpot) {
		this.mappedToSpot = mappedToSpot;
	}

	public double getDistanceToNextPoint() {
		return distanceToNextPoint;
	}

	public void setDistanceToNextPoint(double distanceToNextPoint) {
		this.distanceToNextPoint = distanceToNextPoint;
	}

	public int getLeadsTo() {
		return leadsTo;
	}

	public void setLeadsTo(int leadsTo) {
		this.leadsTo = leadsTo;
	}

	public long getTimediffToNextPoint() {
		return timediffToNextPoint;
	}

	public void setTimediffToNextPoint(long timediffToNextPoint) {
		this.timediffToNextPoint = timediffToNextPoint;
	}
}
