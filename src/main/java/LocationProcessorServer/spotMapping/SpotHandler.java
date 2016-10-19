package LocationProcessorServer.spotMapping;

import java.util.ArrayList;

import LocationProcessorServer.datastructures.*;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;
import entities.*;

/**
 * The SpotHandler is responsible for the main tasks of mapping routes into
 * spots
 * 
 * @author simon_000
 */
public class SpotHandler {

	/**
	 * The calculation level increases every time a trajectory is processed
	 */
	static private int calculationLevel = 0;
	/**
	 * Saves the last spot that the trajectory crossed to check for crossroads
	 */
	static private Spot lastSpot;

	/**
	 * Generates and extends the spot structure
	 * 
	 * @param route
	 *            :Route that is going to be mapped into Spots
	 * @return Route with information about the assigned Spot at each trajectory
	 *         point
	 */
	public static Route learningSpotStructure(Route route) {
		if (route == null || route.isSpotProcessed()) {
		} else {
			if (calculationLevel == 0) {
				route = initialSpotMapping(route);
			} else {
				route = extendSpotStructure(route);
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
	static private Route initialSpotMapping(Route route) {
		// increment calculation-level & mark route as processed
		calculationLevel++;
		route.setSpotProcessed(true);
		// initialize grid-structure

		Grid.createGrid();
		System.out.println("Grid erstellt! -- Datenbankverbindung aufbauen!");
		// create first spot
		Spot spot = generateSpot(route, 0);
		route.getTrajectory().get(0).setSpot(spot);
		route.getTrajectory().get(0).setMappedToSpot(true);
		Grid.add(spot);
		System.out.println(spot + " zur Datenbank hinzufügen");
		// create further spots
		for (int j = 1; j < route.getTrajectory().size(); j++) {
			InfoBundle infobundle = searchClosestSpot(route.getTrajectory().get(j));
			route.getTrajectory().get(j).setClosestSpotInfo(infobundle);
			if (infobundle == null || infobundle.distance >= Spot.stdRadius * 2) {
				spot = generateSpot(route, j);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				Grid.add(spot);
				System.out.println(spot + " zur Datenbank hinzufügen");

			} else if (infobundle.inRange) {
				spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
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
					spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
					route.getTrajectory().get(j).setSpot(spot);
					route.getTrajectory().get(j).setMappedToSpot(true);
				}
				// else add it to the "outside-area" of the nearest spot,
				// because the distance to the nearest spot is too high to be
				// in range and is to close to build an own spot
				else {
					System.out.println("Einen Spot aus der Datenbank laden");
					spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
					route.getTrajectory().get(j).setSpot(spot);
					route.getTrajectory().get(j).setMappedToSpot(true);
				}
			}
			// set neighbors of the created spots
			Spot sp = route.getTrajectory().get(j).getSpot();
			if (sp != null && lastSpot != null) {
				if (sp.getSpotID() != lastSpot.getSpotID()) {
					sp.addNeighbor(lastSpot);
					lastSpot.addNeighbor(sp);
				}
			}
			lastSpot = sp;
		}
		lastSpot = null;

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
	static private Route extendSpotStructure(Route route) {
		calculationLevel++;
		route.setSpotProcessed(true);
		// counts the points that are in the range of the same (already
		// existing) spot
		int inRangeCounter = 0;
		// indicates if the last point was in the range of an existing spot
		boolean lastPointInSpot = false;
		// ID of the last Spot a trajectory point was in range of
		// default = 0
		long lastInRangeID = 0;
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
				Grid.add(spot);
			} else if (!infobundle.inRange && infobundle.distance < (Spot.stdRadius * 2)) {
				// update counter
				inRangeCounter = 0;
				lastPointInSpot = false;
			} else if (infobundle.inRange) {
				// point in range
				spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				route.getTrajectory().get(j).setSpot(spot);
				route.getTrajectory().get(j).setMappedToSpot(true);
				// check if the last point was in the same spot
				if (lastPointInSpot) {
					if (infobundle.minDistance_spotID != lastInRangeID) {
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
				Spot sp = Grid.getSpot(nearestClusterInfo.minDistance_spotID,
						(float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
				Grid.remove(sp);
				// this function will update the spot
				sp.updateSpot(route.getTrajectory().get(minIndex));
				Grid.add(sp);
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
						Spot spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
						route.getTrajectory().get(j).setSpot(spot);
						route.getTrajectory().get(j).setMappedToSpot(true);
					} else {

					}
				} else {
					// check for the current point if its in range of a spot
					if (infobundle.inRange) {
						Spot spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
						route.getTrajectory().get(j).setSpot(spot);
						route.getTrajectory().get(j).setMappedToSpot(true);
					} else if (infobundle.distance > Spot.stdRadius && infobundle.distance < Spot.stdRadius * 2) {
						Spot spot = Grid.getSpot(infobundle.minDistance_spotID, (float)infobundle.minDistance_spotCenterlat,(float)infobundle.minDistance_spotCenterlong);
						route.getTrajectory().get(j).setSpot(spot);
						route.getTrajectory().get(j).setMappedToSpot(true);
					}
				}
			}
			// set neighbors of the created spots
			Spot spot = route.getTrajectory().get(j).getSpot();
			if (spot != null && lastSpot != null) {
				if (spot.getSpotID() != lastSpot.getSpotID()) {
					spot.addNeighbor(lastSpot);
					lastSpot.addNeighbor(spot);
				}
			}
			lastSpot = spot;
		}
		lastSpot = null;
		return route;
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
	static private InfoBundle searchClosestSpot(GPS_plus point) {

		ArrayList<Spot> spots = new ArrayList<Spot>();
		// search close spots with the help of the grid structure
		int x = (int) ((point.getLatitude() - Grid.getMinLat()) / Grid.getGridsize());
		int y = (int) ((point.getLongitude() - Grid.getMinLong()) / Grid.getGridsize());
		// maxsearch: indicates the area of grids around the grid the GPS_plus
		// points lies in that should be searched for spots
		int maxsearch = 4;
		if (Grid.getGridsize() <= 0.005) {
			maxsearch = 6;
		}
		if (Grid.getGridsize() <= 0.001) {
			maxsearch = 10;
		}
		if (Grid.getGridsize() <= 0.0005) {
			maxsearch = 14;
		}
		if (Grid.getGridsize() <= 0.0001) {
			maxsearch = 18;
		}
		ArrayList<Spot> tempSpotList = Grid.getSpots(x, y);
		boolean deepsearch = true;
		if (tempSpotList != null && tempSpotList.size() != 0) {
			for (int i = 0; i < tempSpotList.size();) {
				Spot tempSpot = tempSpotList.get(i);
				if (tempSpot == null) {
					tempSpotList.remove(i);
				} else {
					i++;
				}
			}
			if (tempSpotList.size() != 0) {
				spots.addAll(tempSpotList);
				deepsearch = false;
			}
		}
		if (deepsearch) {
			for (int counter = 1; counter <= maxsearch; counter++) {
				for (int i = (x - counter); i <= (x + counter); i++) {
					for (int j = (y - counter); j <= (y + counter); j++) {
						if ((i > (int) ((Grid.getMaxLat() - Grid.getMinLat()) / Grid.getGridsize())) || (i < 0)) {

						} else if ((j > (int) ((Grid.getMaxLong() - Grid.getMinLong()) / Grid.getGridsize()))
								|| (j < 0)) {

						} else {
							tempSpotList = Grid.getSpots(i, j);
							if (tempSpotList != null) {
								spots.addAll(tempSpotList);
							}
						}
					}
				}
			}
		}

		// the search finished
		// now the closest spot of the found spots must be identified
		double distance;
		double minDistance;
		// Center GPS_plus object of the closest spot
		GPS_plus minDistance_centerGPSdata;
		double minDistance_centerGPSdatalat;
		double minDistance_centerGPSdatalong;
		// ID of the closest spot
		long minDistance_spotID;
		// indicates if the closest spot is in the range of the input GPS_plus
		// object
		boolean inRange = false;

		if (spots.size() != 0) {
			distance = GPSDataProcessor.calcDistance(spots.get(0).getLatitude(),spots.get(0).getLongitude(), point.getLatitude(), point.getLongitude());
			minDistance = distance;
			minDistance_centerGPSdatalat = spots.get(0).getLatitude();
			minDistance_centerGPSdatalong = spots.get(0).getLongitude();

			minDistance_spotID = spots.get(0).getSpotID();
			for (int i = 1; i < spots.size(); i++) {
				distance = GPSDataProcessor.calcDistance(spots.get(i).getLatitude(),spots.get(i).getLongitude(), point.getLatitude(),point.getLongitude());

				if (distance < minDistance) {
					minDistance = distance;
					minDistance_centerGPSdatalat = spots.get(i).getLatitude();
					minDistance_centerGPSdatalong = spots.get(i).getLongitude();

					minDistance_spotID = spots.get(i).getSpotID();
				}
			}
			if (minDistance < Spot.stdRadius) {
				inRange = true;
			}
			return new InfoBundle(minDistance_spotID, minDistance_centerGPSdatalat, minDistance_centerGPSdatalong, inRange, minDistance);
		} else {
			// if there was no spot within the search
			return null;
		}
	}
}
