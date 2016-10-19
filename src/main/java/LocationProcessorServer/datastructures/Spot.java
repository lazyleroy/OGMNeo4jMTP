package LocationProcessorServer.datastructures;

import java.util.ArrayList;

import LocationProcessorServer.geoLibrary.GeoDesy;
import LocationProcessorServer.graphStructure.Node;
import LocationProcessorServer.spotMapping.Line;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;

/**
 * Spots are represented by circles, they can contain GPS-data, and adapt to
 * GPS-data.
 * 
 * @author simon_000
 *
 */
public class Spot {
	/**
	 * ID of the spot
	 */
	private int spotID;
	private static int spotCounter = 0;
	/**
	 * More semantic identifier
	 */
	private String semanticID;

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * Spot center
	 */
	private float longitude;
	private float latitude;
	/**
	 * Heading of the spot
	 */
	private double spotHeading;
	/**
	 * Spot standard radius
	 */
	public static int stdRadius = 25;
	/**
	 * Maximal head-difference for calculation;
	 */
	private static double headDiffThreshold = 30;
	/**
	 * Maximal distance-difference for calculation;
	 */
	private static double distThreshold = 8;
	/**
	 * Neighbored spots
	 */
	private ArrayList<Spot> neighbors;
	/**
	 * Classification as crossroad, or a huge place
	 */
	private boolean intersection;
	/**
	 * Sum of all latitude values used to update the spot center
	 */
	private float latitudeSum;
	/**
	 * Sum of all longitude values used to update the spot center
	 */
	private float longitudeSum;
	/**
	 * Number of points used to update the spot center
	 */
	private int numberCenterCalcPoints;
	/**
	 * Sum of all heading values used to update the spot's heading
	 */
	private double headSum;
	/**
	 * Number of points used to update the spot's heading
	 */
	private int headCalcPoints;
	/**
	 * Indicates if the spot was already checked in terms of identifying nodes 
	 */
	private boolean nodeProcessed;
	/**
	 * Indicates if the spot was already checked in terms of identifying edges 
	 */
	private boolean edgeProcessed;
	/**
	 * Indicates if the spot was already processed in terms of calculating edge weight
	 */
	public boolean weightProcessed;
	/**
	 * Defined Node of this Spot
	 */
	public Node node;

	/**
	 * Default constructor
	 */
	public Spot() {
		// Default
	}

	/**
	 * Constructs a new Spot
	 * 
	 * @param center
	 *            :GPS_plus center-point of the spot
	 * @param head
	 *            :heading direction of the spot
	 */
	public Spot(GPS_plus center, double head) {
		this.latitude = center.getLatitude();
		this.longitude = center.getLongitude();
		this.spotID = Spot.spotCounter++;
		if (head >= 180.0) {
			head = head - 180.0;
		}
		this.spotHeading = head;
		this.neighbors = new ArrayList<Spot>();
		this.intersection = false;
		this.headSum = head;
		this.headCalcPoints = 1;
		this.latitudeSum = center.getLatitude();
		this.longitudeSum = center.getLongitude();
		this.numberCenterCalcPoints = 1;
		this.semanticID = String.valueOf((int) (center.getLatitude() * 1000)) + "_"
				+ String.valueOf((int) (center.getLongitude() * 1000));
	}

	/**
	 * Adds a main point, that is used to update the spot, and calculates
	 * heading & center-point
	 * 
	 * @param point
	 *            :GPS_plus object
	 */
	public void updateSpot(GPS_plus point) {
		double phead = point.getHead();
		if (phead >= 180.0) {
			phead = phead - 180.0;
		}
		double absHeadDiff = Math.abs((phead - spotHeading));
		if (absHeadDiff < headDiffThreshold) {
			this.spotHeading = this.calcSpotHeading(point.getHead());
		}
		double distance = GPSDataProcessor.calcDistance(point.getLatitude(), point.getLongitude(), latitude, longitude);
		if (distance < distThreshold) {
			Line fromSpotCenter = calcLine(latitude,longitude, spotHeading + 90.0);
			Line fromNewPoint = calcLine(point, spotHeading);
			if (fromNewPoint.isVertical() || fromSpotCenter.isVertical()) {
				GPS_plus calcPoint = calcIntersectionVertical(fromSpotCenter, fromNewPoint);
				GPS_plus centerupd = this.clacSpotCenter(calcPoint);
				this.latitude = centerupd.getLatitude();
				this.longitude = centerupd.getLongitude();
			} else {
				GPS_plus calcPoint = calcIntersection(fromSpotCenter, fromNewPoint);
				GPS_plus centerupd = this.clacSpotCenter(calcPoint);
				this.latitude = centerupd.getLatitude();
				this.longitude = centerupd.getLongitude();
			}
		}
		this.semanticID = String.valueOf((int) (latitude * 1000)) + "_"
				+ String.valueOf((int) (longitude * 1000));
	}

	/**
	 * Calculates a new spot heading
	 * 
	 * @return double :new spot heading
	 */
	private double calcSpotHeading(double head) {
		this.headCalcPoints++;
		if (head >= 180.0) {
			head = head - 180.0;
		}
		this.headSum = this.headSum + head;
		double avghead = (headSum / headCalcPoints);
		return avghead;
	}

	/**
	 * Calculates a new spot center
	 * 
	 * @return GPS_plus :new spot center
	 */
	private GPS_plus clacSpotCenter(GPS_plus point) {
		this.numberCenterCalcPoints++;
		this.latitudeSum = this.latitudeSum + point.getLatitude();
		this.longitudeSum = this.longitudeSum + point.getLongitude();
		float avgLat = latitudeSum / numberCenterCalcPoints;
		float avgLong = longitudeSum / numberCenterCalcPoints;

		GPS_plus newCenterPoint = new GPS_plus();
		newCenterPoint.setLatitude(avgLat);
		newCenterPoint.setLongitude(avgLong);
		newCenterPoint.setHead(this.spotHeading);
		return newCenterPoint;
	}

	private GPS_plus clacSpotCenter(float latitude, float longitude) {
		this.numberCenterCalcPoints++;
		this.latitudeSum = this.latitudeSum + latitude;
		this.longitudeSum = this.longitudeSum + longitude;
		float avgLat = latitudeSum / numberCenterCalcPoints;
		float avgLong = longitudeSum / numberCenterCalcPoints;

		GPS_plus newCenterPoint = new GPS_plus();
		newCenterPoint.setLatitude(avgLat);
		newCenterPoint.setLongitude(avgLong);
		newCenterPoint.setHead(this.spotHeading);
		return newCenterPoint;
	}
	
	/**
	 * Adds a new neighbor Spot to the Spot
	 * 
	 * @param spot :Spot to add as neighbor
	 */
	public void addNeighbor(Spot spot){
		if(spot != null){
			double distance = GPSDataProcessor.calcDistance(spot.latitude, spot.longitude, latitude, longitude);
			if(distance >= 30 && distance <= 150){
				if(spot.getSpotID() != this.spotID){
					ArrayList<Spot> neighbors = this.getNeighbors();
					boolean contained = false;
					for (int i = 0; i < neighbors.size(); i++) {
						if (spot.getSpotID() == neighbors.get(i).spotID) {
							contained = true;
						}
					}
					if (!contained) {
						neighbors.add(spot);
						if (neighbors.size() >= 3) {
							this.setIntersection(true);
						}
						this.setNeighbors(neighbors);
					}
				}
			}
		}
	}

	/**
	 * Checks if a point is in the range of the spot
	 * 
	 * @param point
	 *            :GPS_plus object 
	 * @return true if its in range, else false
	 */
	public boolean inRange(GPS_plus point) {
		double dist = GPSDataProcessor.calcDistance(latitude,longitude, point.getLatitude(),point.getLongitude());
		if (dist <= stdRadius) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Calculates a line out of a point and a heading
	 * 
	 * @param point
	 *            :GPS_plus object for calculation
	 * @param head
	 *            :Heading in double for calculation
	 * @return Line - represented as a linear function
	 */
	private Line calcLine(GPS_plus point, double head) {
		GPS_plus temp = new GPS_plus();
		float[] result = new float[3];
		// creates a new point after a arbitrary distance in head direction to
		// calculate a line
		result = GeoDesy.destinationCalculationGEODESY(point.getLatitude(), point.getLongitude(), 50f, (float) head);
		temp.setLatitude(result[1]);
		temp.setLongitude(result[2]);

		Line l = new Line();
		double numerator = (point.getLongitude() - temp.getLongitude());
		double denominator = (point.getLatitude() - temp.getLatitude());
		// set slope m
		l.setM(numerator / denominator);
		// set y-intercept
		l.setB(point.getLongitude() - (l.getM() * point.getLatitude()));
		// check result for correctness and if its a vertical line
		double check = temp.getLongitude() - (l.getM() * temp.getLatitude());
		if (denominator == 0) {
			l.setVertical(true);
			l.setX(point.getLatitude());
		} else {
			double difference = Math.abs(l.getB()-check);
			if (difference >
			0.00000001) {
				System.out.println("line calc went wrong!");
				System.out.println(l.getB());
				System.out.println(check);
				return null;
			}
		}
		return l;
	}

	private Line calcLine(float lati, float longi, double head) {
		GPS_plus temp = new GPS_plus();
		float[] result = new float[3];
		// creates a new point after a arbitrary distance in head direction to
		// calculate a line
		result = GeoDesy.destinationCalculationGEODESY(lati, longi, 50f, (float) head);
		temp.setLatitude(result[1]);
		temp.setLongitude(result[2]);

		Line l = new Line();
		double numerator = (longi - temp.getLongitude());
		double denominator = (lati - temp.getLatitude());
		// set slope m
		l.setM(numerator / denominator);
		// set y-intercept
		l.setB(longi - (l.getM() * lati));
		// check result for correctness and if its a vertical line
		double check = temp.getLongitude() - (l.getM() * temp.getLatitude());
		if (denominator == 0) {
			l.setVertical(true);
			l.setX(lati);
		} else {
			double difference = Math.abs(l.getB()-check);
			if (difference >
					0.00000001) {
				System.out.println("line calc went wrong!");
				System.out.println(l.getB());
				System.out.println(check);
				return null;
			}
		}
		return l;
	}

	/**
	 * Calculates the intersection of two Lines
	 * 
	 * @param l1
	 *            :Line 1
	 * @param l2
	 *            :Line 2
	 * @return GPS_plus - intersection coordinates
	 */
	private GPS_plus calcIntersection(Line l1, Line l2) {
		double latitude = (l1.getB() - l2.getB()) / (l2.getM() - l1.getM());
		double longitude = (l1.getM() * latitude) + l1.getB();
		double check = (l2.getM() * latitude) + l2.getB();
		double diff = Math.abs(longitude - check);
		if (diff > 0.0001) {
			System.out.println(longitude);
			System.out.println(check);
			System.out.println("intersection failed");
			return null;
		}
		GPS_plus temp = new GPS_plus();
		temp.setLatitude((float) latitude);
		temp.setLongitude((float) longitude);
		return temp;
	}

	/**
	 * Calculates the intersection of two Lines if one of them is a vertical
	 * line
	 * 
	 * @param l1
	 *            :Line 1
	 * @param l2
	 *            :Line 2
	 * @return GPS_plus - intersection coordinates
	 */
	private GPS_plus calcIntersectionVertical(Line l1, Line l2) {
		double latitude = 0.0;
		double longitude = 0.0;
		if (l1.isVertical() && l2.isVertical()) {
			return null;
		} else if (l1.isVertical()) {
			latitude = l1.getX();
			longitude = (l2.getM() * latitude) + l2.getB();
		} else if (l2.isVertical()) {
			latitude = l2.getX();
			longitude = (l1.getM() * latitude) + l1.getB();
		}
		GPS_plus temp = new GPS_plus();
		temp.setLatitude((float) latitude);
		temp.setLongitude((float) longitude);
		return temp;
	}

	// Getter & Setter -------------------------------------------------------

	public int getSpotID() {
		return spotID;
	}

	public void setSpotID(int spotID) {
		this.spotID = spotID;
	}

	public String getSemanticID() {
		return semanticID;
	}

	public void setSemanticID(String semanticID) {
		this.semanticID = semanticID;
	}

	/**
	 * @return the neighbors
	 */
	public ArrayList<Spot> getNeighbors() {
		return neighbors;
	}

	/**
	 * @param neighbors
	 *            the neighbors to set
	 */
	public void setNeighbors(ArrayList<Spot> neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * @return the intersection
	 */
	public boolean isIntersection() {
		return intersection;
	}

	/**
	 * @param intersection
	 *            the intersection to set
	 */
	public void setIntersection(boolean intersection) {
		this.intersection = intersection;
	}

	/**
	 * @return the nodeProcessed
	 */
	public boolean isNodeProcessed() {
		return nodeProcessed;
	}

	/**
	 * @param nodeProcessed
	 *            the nodeProcessed to set
	 */
	public void setNodeProcessed(boolean nodeProcessed) {
		this.nodeProcessed = nodeProcessed;
	}

	/**
	 * @return the edgeProcessed
	 */
	public boolean isEdgeProcessed() {
		return edgeProcessed;
	}

	/**
	 * @param edgeProcessed
	 *            the edgeProcessed to set
	 */
	public void setEdgeProcessed(boolean edgeProcessed) {
		this.edgeProcessed = edgeProcessed;
	}
}
