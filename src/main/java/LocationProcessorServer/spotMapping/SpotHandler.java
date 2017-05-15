package LocationProcessorServer.spotMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import LocationProcessorServer.datastructures.*;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;
import config.Neo4jGraphController;
import entities.*;

/**
 * The SpotHandler is responsible for the main tasks of mapping routes into
 * spots
 *
 * @author simon_000
 */
public class SpotHandler {

	Neo4jGraphController neo4j = new Neo4jGraphController();
	/**
	 * The calculation level increases every time a trajectory is processed
	 */
	static private int calculationLevel = 1;
	/**
	 * Saves the last spot that the trajectory crossed to check for crossroads
	 */
	static private Spot lastSpot;

	static public long neo4jTimeOverall = 0;
	static public long addSpotTimeOverall = 0;
	static public long updateSpotTimeOverall = 0;
	static public long getSpotsTimeOverall = 0;
	static public long getSpotTimeOverall = 0;
	static public long addGPSPointsTimeOverall = 0;
	static public long addNeighbourTimeOverall = 0;
	static public long setIntersectionTimeOverall = 0;
	static public long optimizationTimeOverall = 0;
	static public long addGPSPoints1TimeOverall = 0;

	/**
	 * Generates and extends the spot structure
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	public Route learningSpotStructure(Route route) {
		if (route == null || route.isSpotProcessed()) {
		} else {
			if (calculationLevel == 0) {
				route = initialSpotMapping(route);
			} else {
				route = extendSpotStructureSpeedUp_v2(route);
			}
		}
		return route;
	}

	/**
	 * Does the initial spot mapping
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	private Route initialSpotMapping(Route route) {
		Date start_time = new Date();

		// increment calculation-level & mark route as processed
		calculationLevel++;
		route.setSpotProcessed(true);
		// initialize grid-structure
		System.out.println("Grid erstellt! -- Datenbankverbindung aufbauen!");
		// create first spot
		Spot spot = generateSpot(route, 0);
		route.getTrajectory().get(0).setSpot(spot);
		route.getTrajectory().get(0).setMappedToSpot(true);
		neo4j.addSpot(spot);
		// Grid.add(spot);
		System.out.println(spot + " zur Datenbank hinzufügen");
		// create further spots
		for (int j = 1; j < route.getTrajectory().size(); j++) {
			InfoBundle infobundle = searchClosestSpot(route.getTrajectory().get(j));
			route.getTrajectory().get(j).setClosestSpotInfo(infobundle);
			if (infobundle == null || infobundle.distance >= Spot.stdRadius * 2) {
				spot = generateSpot(route, j);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				neo4j.addSpot(spot);
				// Grid.add(spot);
				System.out.println(spot + " zur Datenbank hinzufügen");

			} else if (infobundle.inRange) {
				spot = neo4j.getSpot(infobundle.minDistance_spotID);
				//spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				System.out.println("Einen Spot aus der Datenbank laden");
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
			} else if (!infobundle.inRange && infobundle.distance < Spot.stdRadius * 2) {

			}
		}
		// complete spot mapping and set neighbors of the created spots
		lastSpot = null;
		for (int j = 0; j < route.getTrajectory().size(); j++) {
			// check for the points that wasn't able to build an own spot or
			// wasn't in the range of a spot
			if (!route.getTrajectory().get(j).isMappedToSpot()) {
				// search for the closets spots
				InfoBundle infobundle = searchClosestSpot(route.getTrajectory().get(j));
				route.getTrajectory().get(j).setClosestSpotInfo(infobundle);
				// check for the current point if its in range of a spot
				if (infobundle.inRange) {
					System.out.println("Einen Spot aus der Datenbank laden");
					spot = neo4j.getSpot(infobundle.minDistance_spotID);
					//spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
					route.getTrajectory().get(j).setSpot(spot);
					route.getTrajectory().get(j).setMappedToSpot(true);
				}
				// else add it to the "outside-area" of the nearest spot,
				// because the distance to the nearest spot is too high to be
				// in range and is to close to build an own spot
				else {
					System.out.println("Einen Spot aus der Datenbank laden");
					spot = neo4j.getSpot(infobundle.minDistance_spotID);
					//spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
					route.getTrajectory().get(j).setSpot(spot);
					route.getTrajectory().get(j).setMappedToSpot(true);
				}
			}
			// set neighbors of the created spots
			Spot sp = route.getTrajectory().get(j).getSpot();
			if (sp != null && lastSpot != null) {
				if (!sp.getSpotID().equals(lastSpot.getSpotID())) {
					addNeighbor(lastSpot,spot);
					//sp.addNeighbor(lastSpot);
					//lastSpot.addNeighbor(sp);
					neo4j.addNeighbour(lastSpot.getSpotID(),sp.getSpotID(),lastSpot.isIntersection(),sp.isIntersection());
				}
			}
			lastSpot = sp;
		}
		lastSpot = null;

		Date stop_time = new Date();
		double time = stop_time.getTime() - start_time.getTime();
		time = time / 1000;
		System.out.println("SPOT FINDING: " + time + " seconds");
		return route;
	}

	/**
	 * Extends the spot structure
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	private Route extendSpotStructure(Route route) {
		Date start_time = new Date();

		ArrayList<String> spotIDs = new ArrayList<>();
		calculationLevel++;
		route.setSpotProcessed(true);
		// counts the points that are in the range of the same (already
		// existing) spot
		int inRangeCounter = 0;
		// indicates if the last point was in the range of an existing spot
		boolean lastPointInSpot = false;
		// ID of the last Spot a trajectory point was in range of
		// default = 0
		String lastInRangeID = "";
		// indicates if trajectory was in the last run in the range of a spot
		// and now is immediately in the range of another spot
		boolean changedSpotInRange = false;
		// iterate through the trajectory
		for (int j = 0; j < route.getTrajectory().size(); j++) {
			// search the closest spot
			InfoBundle infobundle = searchClosestSpot(route.getTrajectory().get(j));
			route.getTrajectory().get(j).setClosestSpotInfo(infobundle);

			int tempCounter = inRangeCounter;
			Spot spot;
			// check if the current point is... in range / outside / able to
			// create a new spot
			if (infobundle == null || infobundle.distance >= (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				// generate spot
				spot = generateSpot(route, j);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				neo4j.addSpot(spot);
				//Grid.add(spot);
			} else if (!infobundle.inRange && infobundle.distance < (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
			} else if (infobundle.inRange) {
				// point in range
				spot = neo4j.getSpot(infobundle.minDistance_spotID);
				//spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				// check if the last point was in the same spot
				if (lastPointInSpot) {
					if (!infobundle.minDistance_spotID.equals(lastInRangeID)) {
						inRangeCounter = 0;
						changedSpotInRange = true;
					} else {
						inRangeCounter++;
					}
				} else {
					inRangeCounter++;
				}
				lastInRangeID = infobundle.minDistance_spotID;
				lastPointInSpot = true;
			}
			// Get closest point in range if there was more points in the range
			// of one spot to update the spot
			if (tempCounter > inRangeCounter) {
				// default = 100 - no meaning
				double minDistance = 100;
				int minIndex = 0;
				for (int n = 1; n <= tempCounter; n++) {
					double dist = route.getTrajectory().get(j - n).getClosestSpotInfo().distance;
					if (dist < minDistance) {
						minDistance = dist;
						minIndex = (j - n);
					}
				}
				InfoBundle nearestClusterInfo = route.getTrajectory().get(minIndex).getClosestSpotInfo();
				Spot sp = neo4j.getSpot(infobundle.minDistance_spotID);
				//Spot sp = Grid.getSpot(nearestClusterInfo.minDistance_spotID,
				//(float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				//Grid.remove(sp);
				// this function will update the spot
				sp.updateSpot(route.getTrajectory().get(minIndex));
				neo4j.updateSpot(sp);
				//Grid.add(sp);
			}
			// if the spot in range was changed related to spot of the point
			// before
			if (changedSpotInRange) {
				inRangeCounter = 1;
				changedSpotInRange = false;
			}
		}
		// complete spot mapping and set neighbors of the created spots
		lastSpot = null;
		for (int j = 0; j < route.getTrajectory().size(); j++) {
			// check for the points that wasn't able to build an own spot or
			// wasn't in the range of a spot
			if (!route.getTrajectory().get(j).isMappedToSpot()) {
				GPS_plus currentPoint = route.getTrajectory().get(j);
				InfoBundle infobundle = searchClosestSpot(currentPoint);
				if (infobundle == null) {
					// exception catching: new search
					infobundle = currentPoint.getClosestSpotInfo();
					if (infobundle != null) {
						Spot spot = neo4j.getSpot(infobundle.minDistance_spotID);
						//Spot spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
						route.getTrajectory().get(j).setSpot(spot);
						route.getTrajectory().get(j).setMappedToSpot(true);
					} else {

					}
				} else {
					// check for the current point if its in range of a spot
					if (infobundle.inRange) {
						Spot spot = neo4j.getSpot(infobundle.minDistance_spotID);
						//Spot spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
						route.getTrajectory().get(j).setSpot(spot);
						route.getTrajectory().get(j).setMappedToSpot(true);
					} else if (infobundle.distance > Spot.stdRadius && infobundle.distance < Spot.stdRadius * 2) {
						Spot spot = neo4j.getSpot(infobundle.minDistance_spotID);
						//Spot spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
						route.getTrajectory().get(j).setSpot(spot);
						route.getTrajectory().get(j).setMappedToSpot(true);
					}
				}
			}
			// set neighbors of the created spots
			Spot spot = route.getTrajectory().get(j).getSpot();
			if(j==0){
				spotIDs.add(spot.getSpotID());
			}
			if (spot != null && lastSpot != null) {
				if (!spot.getSpotID().equals(lastSpot.getSpotID())) {
					addNeighbor(lastSpot,spot);
					spotIDs.add(spot.getSpotID());
					//spot.addNeighbor(lastSpot);
					//lastSpot.addNeighbor(spot);
					neo4j.addNeighbour(lastSpot.getSpotID(),spot.getSpotID(),lastSpot.isIntersection(),spot.isIntersection());
				}
			}

			lastSpot = spot;
		}

		lastSpot = null;
		Collections.sort(spotIDs);
		String lastValue = null;
		for(Iterator<String> i = spotIDs.iterator(); i.hasNext();) {
			String currentValue = i.next();
			if(lastValue != null && currentValue.equals(lastValue)) {
				i.remove();
			}
			lastValue = currentValue;
		}
		//neo4j.setIntersections(spotIDs);
		neo4j.addGPSPoints(route.getTrajectory(), route.getUser(),spotIDs);
		Date stop_time = new Date();
		double time = stop_time.getTime() - start_time.getTime();
		time = time / 1000;
		System.out.println("SPOT FINDING: " + time + " seconds");

		return route;
	}

	/**
	 * Extends the spot structure
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	private Route extendSpotStructureSpeedUp(Route route) {
		Date start_time = new Date();
		ArrayList<Integer> notMapped = new ArrayList<>();
		ArrayList<String> spotIDs = new ArrayList<>();
		calculationLevel++;
		route.setSpotProcessed(true);
		// counts the points that are in the range of the same (already
		// existing) spot
		int inRangeCounter = 0;
		// indicates if the last point was in the range of an existing spot
		boolean lastPointInSpot = false;
		// ID of the last Spot a trajectory point was in range of
		// default = 0
		String lastInRangeID = "";
		// indicates if trajectory was in the last run in the range of a spot
		// and now is immediately in the range of another spot
		boolean changedSpotInRange = false;

		//lastSpot
		Spot lastSpot = null;

		Date start,stop;
		start = new Date();

		// iterate through the trajectory
		for (int j = 0; j < route.getTrajectory().size(); j++) {
			// search the closest spot
			InfoBundle infobundle = searchClosestSpot(route.getTrajectory().get(j));
			route.getTrajectory().get(j).setClosestSpotInfo(infobundle);

			// variables
			int tempCounter = inRangeCounter;
			Spot spot = null;


			// check if the current point is... in range / outside / able to
			// create a new spot
			if (infobundle == null || infobundle.distance >= (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				// generate spot
				spot = generateSpot(route, j);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				neo4j.addSpot(spot);
				//Grid.add(spot);

			} else if (!infobundle.inRange && infobundle.distance < (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				notMapped.add(j);

			} else if (infobundle.inRange) {
				// point in range
				spot = infobundle.getSpot();
				//spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				// check if the last point was in the same spot
				if (lastPointInSpot) {
					if (!infobundle.minDistance_spotID.equals(lastInRangeID)) {
						inRangeCounter = 0;
						changedSpotInRange = true;
					} else {
						inRangeCounter++;
					}
				} else {
					inRangeCounter++;
				}
				lastInRangeID = infobundle.minDistance_spotID;
				lastPointInSpot = true;


			}

			// Get closest point in range if there was more points in the range
			// of one spot to update the spot
			if (tempCounter > inRangeCounter) {
				// default = 100 - no meaning
				double minDistance = 100;
				int minIndex = 0;
				for (int n = 1; n <= tempCounter; n++) {
					double dist = route.getTrajectory().get(j - n).getClosestSpotInfo().distance;
					if (dist < minDistance) {
						minDistance = dist;
						minIndex = (j - n);
					}
				}
				InfoBundle nearestClusterInfo = route.getTrajectory().get(minIndex).getClosestSpotInfo();
				Spot sp = nearestClusterInfo.getSpot();
				// this function will update the spot
				sp.updateSpot(route.getTrajectory().get(minIndex));
				neo4j.updateSpot(sp);
				//Grid.add(sp);
			}

			// if the spot in range was changed related to spot of the point
			// before
			if (changedSpotInRange) {
				inRangeCounter = 1;
				changedSpotInRange = false;
			}

			if(j==0){
				if(spot != null) {
					spotIDs.add(spot.getSpotID());
				}
			}
			if (spot != null && lastSpot != null) {
				if (!spot.getSpotID().equals(lastSpot.getSpotID())) {
					addNeighbor(lastSpot,spot);
					//neo4j.addNeighbour(lastSpot.getSpotID(),spot.getSpotID(),lastSpot.isIntersection(),spot.isIntersection());
					spotIDs.add(spot.getSpotID());
				}
			}
			if(spot != null){
				lastSpot = spot;
			}
		}
		lastSpot = null;
		stop = new Date();
		System.out.println("Time: 1st map run: "+(stop.getTime()-start.getTime()));

		start = new Date();
		// complete spot mapping
		for (int k = 0; k < notMapped.size(); k++) {
			// check for the points that wasn't able to build an own spot or
			// wasn't in the range of a spot
			GPS_plus currentPoint = route.getTrajectory().get(notMapped.get(k));

			Spot closestSpot = null;
			Spot currentclosestSpot = route.getTrajectory().get(notMapped.get(k)).getClosestSpotInfo().getSpot();
			for (int temp = notMapped.get(k) + 1; temp < route.size(); temp++) {
				if (route.getTrajectory().get(temp).isMappedToSpot()) {
					Spot nextSpot = route.getTrajectory().get(temp).getSpot();
					double distance = GPSDataProcessor.calcDistance(nextSpot.getLatitude(), nextSpot.getLongitude(),
							route.getTrajectory().get(temp).getLatitude(), route.getTrajectory().get(temp).getLongitude());
					if (distance < route.getTrajectory().get(notMapped.get(k)).getClosestSpotInfo().minDistance_spotCenterlat) {
						closestSpot = nextSpot;
					} else {
						closestSpot = currentclosestSpot;
					}
					break;
				} else {
					closestSpot = currentclosestSpot;
				}
			}
			route.getTrajectory().get(notMapped.get(k)).setSpot(closestSpot);
			route.getTrajectory().get(notMapped.get(k)).setMappedToSpot(true);
		}
		stop = new Date();
		System.out.println("Time: map unmapped points "+(stop.getTime()-start.getTime()));
		start = new Date();

		Collections.sort(spotIDs);

		String lastValue = null;
		for(Iterator<String> i = spotIDs.iterator(); i.hasNext();) {
			String currentValue = i.next();
			if(lastValue != null && currentValue.equals(lastValue)) {
				i.remove();
			}
			lastValue = currentValue;
		}

		neo4j.addGPSPoints(route.getTrajectory(), route.getUser(), spotIDs);

		stop = new Date();
		System.out.println("Time: finish: "+(stop.getTime()-start.getTime()));

		return route;
	}

	/**
	 * Extends the spot structure
	 *
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	private Route extendSpotStructureSpeedUp_v2(Route route) {
		Date start = new Date();
		ArrayList<Integer> notMapped = new ArrayList<>();
		ArrayList<String> spotIDs = new ArrayList<>();
		calculationLevel++;
		route.setSpotProcessed(true);
		// counts the points that are in the range of the same (already
		// existing) spot
		int inRangeCounter = 0;
		// indicates if the last point was in the range of an existing spot
		boolean lastPointInSpot = false;
		// ID of the last Spot a trajectory point was in range of
		// default = 0
		String lastInRangeID = "";
		// indicates if trajectory was in the last run in the range of a spot
		// and now is immediately in the range of another spot
		boolean changedSpotInRange = false;

		//lastSpot
		Spot lastSpot = null;

		// iterate through the trajectory
		for (int j = 0; j < route.getTrajectory().size(); j++) {
			// search the closest spot
			InfoBundle infobundle;
			GPS_plus gpsPoint = route.getTrajectory().get(j);
			double distanceToLastSpot = 1000000;
			if(lastSpot !=null){
				distanceToLastSpot = GPSDataProcessor.calcDistance(gpsPoint.getLatitude(),gpsPoint.getLongitude(),lastSpot.getLatitude(),lastSpot.getLongitude());
			}
			if(distanceToLastSpot < Spot.stdRadius){
				InfoBundle bundle = new InfoBundle(lastSpot.getSpotID(), lastSpot.getLatitude(), lastSpot.getLongitude(), true, distanceToLastSpot);
				bundle.setSpot(lastSpot);
				infobundle = bundle;
			}
			else {
				infobundle = searchClosestSpot(route.getTrajectory().get(j));
			}
			route.getTrajectory().get(j).setClosestSpotInfo(infobundle);

			// variables
			int tempCounter = inRangeCounter;
			Spot spot = null;

			// check if the current point is... in range / outside / able to
			// create a new spot
			if (infobundle == null || infobundle.distance >= (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				// generate spot
				spot = generateSpot(route, j);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				neo4j.addSpot(spot);
				//Grid.add(spot);
			} else if (!infobundle.inRange && infobundle.distance < (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
				notMapped.add(j);

			} else if (infobundle.inRange) {
				// point in range
				spot = infobundle.getSpot();
				//spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				// check if the last point was in the same spot
				if (lastPointInSpot) {
					if (!infobundle.minDistance_spotID.equals(lastInRangeID)) {
						inRangeCounter = 0;
						changedSpotInRange = true;
					} else {
						inRangeCounter++;
					}
				} else {
					inRangeCounter++;
				}
				lastInRangeID = infobundle.minDistance_spotID;
				lastPointInSpot = true;
			}

			// Get closest point in range if there was more points in the range
			// of one spot to update the spot
			if (tempCounter > inRangeCounter) {
				// default = 100 - no meaning
				double minDistance = 100;
				int minIndex = 0;
				for (int n = 1; n <= tempCounter; n++) {
					double dist = route.getTrajectory().get(j - n).getClosestSpotInfo().distance;
					if (dist < minDistance) {
						minDistance = dist;
						minIndex = (j - n);
					}
				}
				InfoBundle nearestClusterInfo = route.getTrajectory().get(minIndex).getClosestSpotInfo();
				Spot sp = nearestClusterInfo.getSpot();
				// this function will update the spot
				sp.updateSpot(route.getTrajectory().get(minIndex));
				neo4j.updateSpot(sp);
				//Grid.add(sp);
			}

			// if the spot in range was changed related to spot of the point
			// before
			if (changedSpotInRange) {
				inRangeCounter = 1;
				changedSpotInRange = false;
			}

			if(j==0){
				if(spot != null) {
					spotIDs.add(spot.getSpotID());
				}
			}
			if (spot != null && lastSpot != null) {
				if (!spot.getSpotID().equals(lastSpot.getSpotID())) {
					addNeighbor(lastSpot,spot);
					//neo4j.addNeighbour(lastSpot.getSpotID(),spot.getSpotID(),lastSpot.isIntersection(),spot.isIntersection());
					spotIDs.add(spot.getSpotID());
				}
			}
			if(spot != null){
				lastSpot = spot;
			}
		}

		// complete spot mapping
		for (int k = 0; k < notMapped.size(); k++) {
			// check for the points that wasn't able to build an own spot or
			// wasn't in the range of a spot
			GPS_plus currentPoint = route.getTrajectory().get(notMapped.get(k));

			Spot closestSpot = null;
			Spot currentclosestSpot = route.getTrajectory().get(notMapped.get(k)).getClosestSpotInfo().getSpot();
			for (int temp = notMapped.get(k) + 1; temp < route.size(); temp++) {
				if (route.getTrajectory().get(temp).isMappedToSpot()) {
					Spot nextSpot = route.getTrajectory().get(temp).getSpot();
					double distance = GPSDataProcessor.calcDistance(nextSpot.getLatitude(), nextSpot.getLongitude(),
							route.getTrajectory().get(temp).getLatitude(), route.getTrajectory().get(temp).getLongitude());
					if (distance < route.getTrajectory().get(notMapped.get(k)).getClosestSpotInfo().minDistance_spotCenterlat) {
						closestSpot = nextSpot;
					} else {
						closestSpot = currentclosestSpot;
					}
					break;
				} else {
					closestSpot = currentclosestSpot;
				}
				if(temp > notMapped.get(k) + 5){
					break;
				}
			}
			route.getTrajectory().get(notMapped.get(k)).setSpot(closestSpot);
			route.getTrajectory().get(notMapped.get(k)).setMappedToSpot(true);
		}

		Collections.sort(spotIDs);
		String lastValue = null;
		for(Iterator<String> i = spotIDs.iterator(); i.hasNext();) {
			String currentValue = i.next();
			if(lastValue != null && currentValue.equals(lastValue)) {
				i.remove();
			}
			lastValue = currentValue;
		}
		neo4j.addGPSPoints(route.getTrajectory(), route.getUser(), spotIDs);

		Date stop = new Date();




		long overallTime = stop.getTime()-start.getTime();
		System.out.println("OVERALL TIME: SINGLE ROUTE: "+ overallTime);

		long neo4jtime = neo4j.addSpotTime
						+neo4j.updateSpotTime
						+neo4j.getSpotsTime
						+neo4j.addGPSPointsTime
						+neo4j.addNeighbourTime
						+neo4j.getSpotTime
						+neo4j.setIntersectionsTime;

		System.out.println("\t NEO4J TIME: "+ neo4jtime+ "\t\t\t\t AVERAGE NEO4J TIME PER NODE: "+ (double)neo4jtime/route.size());
		System.out.println("\t\t ADD SPOTs - TIME: "+neo4j.addSpotTime+ "\t\t\t\t AVERAGE ADD SPOTs - TIME PER NODE: "+ (double)neo4j.addSpotTime/route.size());
		System.out.println("\t\t UPDATE SPOTs - TIME: "+neo4j.updateSpotTime+ "\t\t\t\t AVERAGE UPDATE SPOTs - TIME PER NODE: "+ (double)neo4j.updateSpotTime/route.size());
		System.out.println("\t\t GET SPOT - TIME: "+neo4j.getSpotTime+ "\t\t\t\t AVERAGE GET SPOT - TIME PER NODE: "+ (double)neo4j.getSpotTime/route.size());
		System.out.println("\t\t GET SPOTs - TIME: "+neo4j.getSpotsTime+ "\t\t\t\t AVERAGE GET SPOTS - TIME PER NODE: "+ (double)neo4j.getSpotsTime/route.size());
		System.out.println("\t\t ADD GPSPOINTs - TIME: "+neo4j.addGPSPointsTime+ "\t\t\t\t AVERAGE ADD GPSPOINTS - TIME PER NODE: "+ (double)neo4j.addGPSPointsTime/route.size());
		System.out.println("\t\t\t ADD GPSPOINTs1 - TIME: "+neo4j.addGPSPointsTime1+ "\t\t\t\t AVERAGE ADD GPSPOINTS1 - TIME PER NODE: "+ (double)neo4j.addGPSPointsTime1/route.size());
		System.out.println("\t\t ADD NEIGHBOURs - TIME: "+neo4j.addNeighbourTime+ "\t\t\t\t AVERAGE ADD NEIGHBOURS - TIME PER NODE: "+ (double)neo4j.addNeighbourTime/route.size());
		System.out.println("\t\t SET INTERSECTION - TIME: "+neo4j.setIntersectionsTime+ "\t\t\t\t AVERAGE INTERSECTION - TIME PER NODE: "+ (double)neo4j.setIntersectionsTime/route.size());
		System.out.println("\t OPTIMIZATION - TIME: "+(overallTime-neo4jtime)+ "\t\t\t\t AVERAGE OPTIMIZATION - TIME PER NODE: "+ (double)(overallTime-neo4jtime)/route.size());
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.println();
		neo4jTimeOverall = neo4jTimeOverall + neo4jtime;
		addSpotTimeOverall = addSpotTimeOverall + neo4j.addSpotTime;
		addGPSPointsTimeOverall = addGPSPointsTimeOverall + neo4j.addGPSPointsTime;
		addGPSPoints1TimeOverall = addGPSPoints1TimeOverall + neo4j.addGPSPointsTime1;
		addNeighbourTimeOverall = addNeighbourTimeOverall + neo4j.addNeighbourTime;
		updateSpotTimeOverall = updateSpotTimeOverall + neo4j.updateSpotTime;
		getSpotsTimeOverall = getSpotsTimeOverall + neo4j.getSpotsTime;
		setIntersectionTimeOverall += neo4j.setIntersectionsTime;
		getSpotTimeOverall += neo4j.getSpotTime;
		optimizationTimeOverall += (overallTime-neo4jtime);

		neo4j.addSpotTime = 0;
		neo4j.updateSpotTime = 0;
		neo4j.addGPSPointsTime = 0;
		neo4j.addGPSPointsTime1 = 0;
		neo4j.addNeighbourTime = 0;
		neo4j.setIntersectionsTime = 0;
		neo4j.getSpotTime = 0;
		neo4j.getSpotsTime = 0;

		return route;
	}

	/**
	 * Adds a new neighbor Spot to the Spot
	 *
	 * @param spot :Spot to add as neighbor
	 */
	public void addNeighbor(Spot spot, Spot spot2) {
		if (spot != null) {
			double distance = GPSDataProcessor.calcDistance(spot.getLatitude(), spot.getLongitude(), spot2.getLatitude(), spot2.getLongitude());
			if (distance >= 25 && distance <= 150) {
				if (!spot.getSpotID().equals(spot2.getSpotID())) {
					if(spot.addNeighborAlternative(spot2) & spot2.addNeighborAlternative(spot)){
						neo4j.addNeighbour(spot.getSpotID(),spot2.getSpotID(),spot.isIntersection(),spot2.isIntersection());
					}
				}
			}
		}
	}

	/**
	 * Generates a new spot out of a point(GPS_plus object) with given index in
	 * a specific route
	 *
	 * @param route
	 *            :Route that contains the GPS_plus object
	 * @param indexGPSpoint
	 *            :Index of the point, which generates the Spot
	 * @return Created Spot
	 */
	private static Spot generateSpot(Route route, int indexGPSpoint) {
		GPS_plus currentPoint = route.getTrajectory().get(indexGPSpoint);
		double heading = currentPoint.getHead();
		Spot spot = new Spot(currentPoint, heading);
		return spot;
	}

	/**
	 * Calculates the closest spot of a GPS point (GPS_plus object)
	 *
	 * @param point
	 *            :GPS_plus point
	 * @return InfoBundle, gives information about the closest Spot (see
	 *         documentation InfoBundle)
	 */
	private InfoBundle searchClosestSpot(GPS_plus point) {

		System.out.println("searchClosestSpot");
		ArrayList<Spot> spots =  neo4j.getSpots(point.getLatitude(),point.getLongitude());


		// the search finished
		// now the closest spot of the found spots must be identified
		double distance;
		double minDistance;
		String minDistance_spotID;

		// Center GPS_plus object of the closest spot
		double minDistance_centerGPSdatalat;
		double minDistance_centerGPSdatalong;

		// indicates if the closest spot is in the range of the input GPS_plus
		// object
		boolean inRange = false;

		if (spots != null && spots.size() != 0) {
			distance = GPSDataProcessor.calcDistance(spots.get(0).getLatitude(),spots.get(0).getLongitude(), point.getLatitude(), point.getLongitude());
			minDistance = distance;
			minDistance_centerGPSdatalat = spots.get(0).getLatitude();
			minDistance_centerGPSdatalong = spots.get(0).getLongitude();
			int spotArrayPosition = 0;

			minDistance_spotID = spots.get(0).getSpotID();
			for (int i = 1; i < spots.size(); i++) {
				distance = GPSDataProcessor.calcDistance(spots.get(i).getLatitude(),spots.get(i).getLongitude(), point.getLatitude(),point.getLongitude());
				if (distance < minDistance) {
					minDistance = distance;
					minDistance_centerGPSdatalat = spots.get(i).getLatitude();
					minDistance_centerGPSdatalong = spots.get(i).getLongitude();
					minDistance_spotID = spots.get(i).getSpotID();
					spotArrayPosition = i;
				}
			}
			if (minDistance < Spot.stdRadius) {
				inRange = true;
			}
			InfoBundle bundle = new InfoBundle(minDistance_spotID, minDistance_centerGPSdatalat, minDistance_centerGPSdatalong, inRange, minDistance);
			bundle.setSpot(spots.get(spotArrayPosition));
			return bundle;
		} else {
			// if there was no spot within the search
			return null;
		}
	}
}
