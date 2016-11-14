package LocationProcessorServer.trajectoryPreparation;

import java.util.ArrayList;
import entities.*;
import java.util.Date;
import cern.colt.matrix.impl.SparseObjectMatrix2D;
import LocationProcessorServer.datastructures.*;
import LocationProcessorServer.geoLibrary.GeoDesy;
import LocationProcessorServer.spotMapping.Grid;

/**
 * This class processes GPS-data, splits GPS-trajectories into separate routes
 * and deletes erroneous data
 * 
 * @author simon_000
 *
 */
public class GPSDataProcessor {

	/**
	 * Time-threshold to split a trajectory into separate routes (300000 equals
	 * 5min)
	 */
	private static long thresholdSplitTime = 300000;
	/**
	 * Distance-threshold to split a trajectory into separate routes
	 */
	private static double thresholdSplitDistance = 200;
	/**
	 * Time-span-threshold (in seconds) to delete erroneous data
	 */
	private static int thresholdMinTimespan = 0;
	/**
	 * Speed-threshold to delete data above the defined speed (in m/s)
	 */
	private static int thresholdMaxSpeed = 100;
	/**
	 * Distance-threshold in meters to delete multiple recorded GPS-points
	 */
	private static double thresholdMinDistance = 0;
	/**
	 * Threshold for the minimum number of points in a route (to be considered)
	 */
	private static int thresholdMinDatapoints = 10;
	/**
	 * Threshold for the interpolation (in meter)
	 */
	private static int interpolationTreshold = 15;
	/**
	 * Threshold for fill missing segments (in meter)
	 */
	private static int missingSegementTreshold = 40;
	/**
	 * DistanceValue for the interpolation (in meter)
	 */
	private static int interpolationValue = 5;
	// -----------------------
	// Parameters for DBSCAN
	// -----------------------
	/**
	 * Defines the epsilon in meters to find core points
	 */
	private static int DBSCANeps = 10;
	/**
	 * Defines the minimum number of points to determine a core-point
	 */
	private static int DBSCANminPts = 50;
	/**
	 * Maximal time-span that the points within DBSCANeps has to fulfill to be
	 * considered (in seconds) - currently not used (therefore this high value)
	 */
	private static int DBSCANminTime = 1000000000;
	/**
	 * Defines the epsilon in meters for searching further neighbors outgoing
	 * from border points
	 */
	private static int DBSCANepsB = 5;

	/**
	 * Splits GPS-data lists after users
	 * 
	 * @param gps_datalist
	 *            :List of GPS_plus objects
	 * @return Trajectory for every user (in an ArrayList)
	 */
	static public ArrayList<Trajectory> splitTrajectoryByUser(ArrayList<GPS_plus> gps_datalist) {
		ArrayList<Trajectory> userTrajectories = new ArrayList<Trajectory>();
		ArrayList<String> temporaryUserlist = new ArrayList<String>();
		for (int i = 0; i < gps_datalist.size(); i++) {
			String user = gps_datalist.get(i).getUserID();
			if (temporaryUserlist.contains(user)) {
				for (int j = 0; j < userTrajectories.size(); j++) {
					if (userTrajectories.get(j).getUser().equals(user)) {
						userTrajectories.get(j).getTrajectory().add(gps_datalist.get(i));
					}
				}
			} else {
				temporaryUserlist.add(user);
				Trajectory userTrajectory = new Trajectory(user);
				userTrajectory.getTrajectory().add(gps_datalist.get(i));
				userTrajectories.add(userTrajectory);
			}
		}
		return userTrajectories;
	}

	/**
	 * Sorts a trajectory by time
	 * 
	 * @param userdata
	 *            :Trajectory that is going to be sorted
	 * @return sorted Trajectory
	 */
	static private Trajectory sortTrajectoryByTime(Trajectory userdata) {
		Trajectory sorted = new Trajectory(userdata.getUser());
		int size = userdata.getTrajectory().size();
		for (int i = 0; i < size; i++) {
			sorted.getTrajectory().add(extractMinTime(userdata.getTrajectory()));
		}
		return sorted;
	}

	/**
	 * Sorts a trajectory by time
	 * 
	 * @param route
	 *            :Route that is going to be sorted
	 * @return sorted Route
	 */
	@SuppressWarnings("unused")
	static private Route sortRouteByTime(Route route) {
		ArrayList<GPS_plus> sorted = new ArrayList<GPS_plus>();
		ArrayList<GPS_plus> unsorted = route.getTrajectory();
		for (int i = 0; i < unsorted.size(); i++) {
			sorted.add(extractMinTime(unsorted));
		}
		route.setTrajectory(sorted);
		return route;
	}

	/**
	 * Searches the GPS-point with the smallest timestamp, returns it and
	 * removes it from the list
	 * 
	 * @param gps_list
	 *            :List with GPS_plus objects
	 * @return GPS_plus object with the smallest timestamp
	 */
	static private GPS_plus extractMinTime(ArrayList<GPS_plus> gps_list) {
		long minTime = gps_list.get(0).getTime().getTime();
		int minTimeListIndex = 0;

		for (int i = 1; i < gps_list.size(); i++) {
			long time = gps_list.get(i).getTime().getTime();
			if (time < minTime) {
				minTime = time;
				minTimeListIndex = i;
			}
		}
		return gps_list.remove(minTimeListIndex);
	}

	/**
	 * Splits a Trajectory into different routes
	 * 
	 * @param t
	 *            :Trajectory to be split
	 * @return ArrayList of Routes
	 */
	static public ArrayList<Route> splitTrajectoryByRoutes(Trajectory t) {
		// sort Trajectory
		t = sortTrajectoryByTime(t);
		// delete erroneous data points
		t = deleteErrors(t);
		// split cleaned trajectory
		ArrayList<Route> routes = new ArrayList<Route>();
		ArrayList<GPS_plus> trajectory = t.getTrajectory();
		ArrayList<GPS_plus> temporary_list = new ArrayList<GPS_plus>();
		if (trajectory.size() > 0) {
			for (int i = 0; i < trajectory.size() - 1; i++) {
				temporary_list.add(trajectory.get(i));
				// time difference to the next GPS point in trajectory
				long timeDiff = trajectory.get(i).getTimediffToNextPoint();
				// distance difference to the next GPS point in trajectory
				double distanceDiff = trajectory.get(i).getDistanceToNextPoint();

				// if time difference or distance is too high, split
				if (timeDiff >= thresholdSplitTime || distanceDiff >= thresholdSplitDistance) {

					// delete stay-points like home, work & stops in between
					temporary_list = deleteStaypoints(temporary_list, DBSCANeps, DBSCANepsB, DBSCANminPts,
							DBSCANminTime);

					// check if the route contains enough GPS-points
					if (temporary_list.size() >= thresholdMinDatapoints) {
						Route route = new Route(new ArrayList<GPS_plus>(temporary_list), t.getUser());
						// fill missing segments and interpolate
						route = improveRouteData(route);
						// route = sortRouteByTime(route);
						routes.add(route);
					}
					temporary_list.clear();
				}
			}
			// process the remained data
			if (trajectory.size() > 1) {
				temporary_list.add(trajectory.get(trajectory.size() - 1));
				trajectory.get(trajectory.size() - 1).setHead(trajectory.get(trajectory.size() - 2).getHead());
			}
			// delete stay-points like home, work & halts in between
			temporary_list = deleteStaypoints(temporary_list, DBSCANeps, DBSCANepsB, DBSCANminPts, DBSCANminTime);

			// check if the route contains enough GPS-points
			if (temporary_list.size() >= thresholdMinDatapoints) {
				Route route = new Route(new ArrayList<GPS_plus>(temporary_list), t.getUser());
				// fill missing segments and interpolate
				route = improveRouteData(route);
				// route = sortRouteByTime(route);
				routes.add(route);
			}
			// return the resulting routes
			return routes;
		} else {
			return null;
		}
	}

	/**
	 * Fill missing segments in routes and interpolate the route data
	 * 
	 * @param route
	 *            :Input-Route
	 * @return processed Route
	 */
	static private Route improveRouteData(Route route) {
		ArrayList<GPS_plus> trajectory = route.getTrajectory();
		// smooth the route trajectory
		route = smoothTrajectory(route);
		for (int i = 0; i < trajectory.size() - 1; i++) {
			// calculate time, distance and speed
			double distanceDiff = calcDistance(trajectory.get(i), trajectory.get(i + 1));
			long timeDiff = timeDiff(trajectory.get(i + 1), trajectory.get(i));
			double heading = calcHeading(trajectory.get(i), trajectory.get(i + 1));
			double timeInS = timeDiff / 1000;
			double speedinMperS = distanceDiff / timeInS;
			trajectory.get(i).setDistanceToNextPoint(distanceDiff);
			trajectory.get(i).setTimediffToNextPoint(timeDiff);
			trajectory.get(i).setHead(heading);
			trajectory.get(i).setSpeed(speedinMperS);

			// check if segments must be filled
			if (distanceDiff >= missingSegementTreshold) {
				int calc1 = (int) (i / 2);
				int calc2 = (int) ((trajectory.size() - i) / 2);
				int k;
				if (calc1 > calc2) {
					k = calc1;
				} else {
					k = calc2;
				}
				fillMissingSegments(route, i, i + 1, k);
			}
		}
		for (int i = 0; i < trajectory.size() - 1; i++) {
			double distanceDiff = trajectory.get(i).getDistanceToNextPoint();
			// check if interpolation is necessary
			if (distanceDiff >= interpolationTreshold) {
				interpolation(route, i, i + 1);
			}
		}
		return route;
	}

	/**
	 * Deletes stay-points from a GPS Trajectory
	 * 
	 * @param trajectory
	 *            :Input-list of GPS_plus objects
	 * @param eps
	 *            :defines the epsilon in meters for the DBSCAN-neighbor-search
	 *            to find core points
	 * @param epsB
	 *            :defines the epsilon in meters for searching further neighbors
	 *            outgoing from border points
	 * @param minPts
	 *            :defines the minimum number of points to determine a
	 *            core-point
	 * @param minTime
	 *            :Maximal time-span that the points within DBSCANeps has to
	 *            fulfill to be considered (in seconds)
	 * @return ArrayList<GPS_plus> Output-list
	 */
	static private ArrayList<GPS_plus> deleteStaypoints(ArrayList<GPS_plus> trajectory, int eps, int epsB, int minPts,
			int minTime) {
		// map trajectory points into a temporary grid
		double gridsize = 0.001;
		int rows = (int) ((Grid.getMaxLat() - Grid.getMinLat()) / gridsize);
		int cols = (int) ((Grid.getMaxLong() - Grid.getMinLong()) / gridsize);
		SparseObjectMatrix2D matrix = new SparseObjectMatrix2D(rows, cols);
		for (int i = 0; i < trajectory.size(); i++) {
			int x = (int) ((trajectory.get(i).getLatitude() - Grid.getMinLat()) / gridsize);
			int y = (int) ((trajectory.get(i).getLongitude() - Grid.getMinLong()) / gridsize);
			@SuppressWarnings("unchecked")
			ArrayList<GPS_plus> data = (ArrayList<GPS_plus>) matrix.getQuick(x, y);
			if (data != null) {
				data.add(trajectory.get(i));
				matrix.set(x, y, data);
			} else {
				data = new ArrayList<GPS_plus>();
				data.add(trajectory.get(i));
				matrix.setQuick(x, y, data);
			}

		}
		ArrayList<DBSCANCluster> staypoints = new ArrayList<DBSCANCluster>();
		// Get Points in a specific range: "neighbors"
		for (int i = 0; i < trajectory.size(); i++) {
			trajectory.get(i).setNeighborsDBSCAN(new ArrayList<GPS_plus>());
			if (!trajectory.get(i).isProcessedDBSCAN()) {
				// map current GPS point into grid and get other points there
				int x = (int) ((trajectory.get(i).getLatitude() - Grid.getMinLat()) / gridsize);
				int y = (int) ((trajectory.get(i).getLongitude() - Grid.getMinLong()) / gridsize);
				ArrayList<GPS_plus> closeDatapoints = new ArrayList<GPS_plus>();
				for (int k = (x - 2); k <= (x + 2); k++) {
					for (int n = (y - 2); n <= (y + 2); n++) {
						if ((k >= (int) ((Grid.getMaxLat() - Grid.getMinLat()) / gridsize)) || (k < 0)) {

						} else if ((n >= (int) ((Grid.getMaxLong() - Grid.getMinLong()) / gridsize)) || (n < 0)) {

						} else {
							@SuppressWarnings("unchecked")
							ArrayList<GPS_plus> temp = (ArrayList<GPS_plus>) matrix.get(k, n);
							if (temp != null) {
								closeDatapoints.addAll(temp);
							}
						}
					}
				}
				// search neighbors
				for (int j = 0; j < closeDatapoints.size(); j++) {
					double diff = calcDistance(trajectory.get(i), closeDatapoints.get(j));
					long timediff = trajectory.get(i).getTime().getTime() - closeDatapoints.get(j).getTime().getTime();
					if (timediff < 0) {
						timediff = timediff * -1;
					}
					if (diff <= eps && diff != 0.0) {
						if (timediff < minTime) {
							trajectory.get(i).getNeighborsDBSCAN().add(closeDatapoints.get(j));
						}
					}
				}
				// if all neighbors are found, check if processed point is a
				// core-point
				if (trajectory.get(i).getNeighborsDBSCAN().size() > DBSCANminPts) {

					// create cluster & set values
					DBSCANCluster cl = new DBSCANCluster(trajectory.get(i));
					cl.borderPoints = trajectory.get(i).getNeighborsDBSCAN();
					cl.numberOfPts = trajectory.get(i).getNeighborsDBSCAN().size();
					trajectory.get(i).setPointInfoDBSAN("core");
					trajectory.get(i).setProcessedDBSCAN(true);
					trajectory.get(i).setClusterDBSCAN(cl);
					// go through neighbors and mark them
					for (int z = 0; z < trajectory.get(i).getNeighborsDBSCAN().size(); z++) {
						GPS_plus n = trajectory.get(i).getNeighborsDBSCAN().get(z);
						for (int j = 0; j < trajectory.size(); j++) {
							if (n.getDataID() == trajectory.get(j).getDataID()) {
								trajectory.get(j).setClusterDBSCAN(cl);
								trajectory.get(j).setPointInfoDBSAN("border");
							}
						}
					}
					// go through neighbors and search for further border points
					for (int z = 0; z < trajectory.get(i).getNeighborsDBSCAN().size(); z++) {
						GPS_plus n = trajectory.get(i).getNeighborsDBSCAN().get(z);
						for (int j = 0; j < trajectory.size(); j++) {
							if (trajectory.get(j).getPointInfoDBSAN() == null) {
								double diff = calcDistance(n, trajectory.get(j));
								if (diff <= epsB) {
									trajectory.get(i).getNeighborsDBSCAN().add(trajectory.get(j));
									trajectory.get(j).setPointInfoDBSAN("border");
									trajectory.get(j).setClusterDBSCAN(cl);
									cl.addPoint(trajectory.get(j));
								}
							}
						}
					}
					staypoints.add(cl);
					// go through neighbors and mark all as processed
					for (int z = 0; z < trajectory.get(i).getNeighborsDBSCAN().size(); z++) {
						GPS_plus n = trajectory.get(i).getNeighborsDBSCAN().get(z);
						for (int j = 0; j < trajectory.size(); j++) {
							if (n.getDataID() == trajectory.get(j).getDataID()) {
								trajectory.get(j).setProcessedDBSCAN(true);
							}
						}
					}
				}
			}

		}
		// remove cluster-points from data
		ArrayList<GPS_plus> pointsToRemove = new ArrayList<GPS_plus>();
		for (int i = 0; i < staypoints.size(); i++) {
			pointsToRemove.addAll(staypoints.get(i).borderPoints);
			pointsToRemove.add(staypoints.get(i).corepoint);
		}
		for (int j = 0; j < pointsToRemove.size(); j++) {
			for (int k = 0; k < trajectory.size();) {
				if (pointsToRemove.get(j).getDataID() == trajectory.get(k).getDataID()) {
					trajectory.remove(k);
				} else {
					k++;
				}
			}
		}
		return trajectory;
	}

	/**
	 * Interpolation between 2 trajectory points
	 * 
	 * @param route
	 *            :Route for interpolation
	 * @param index1
	 *            :'start-index' of the interpolation segment
	 * @param index2
	 *            :'end-index' of the interpolation segment
	 * 
	 * @return processed Route
	 */
	static private Route interpolation(Route route, int index1, int index2) {
		ArrayList<GPS_plus> list = route.getTrajectory();
		// Calculate N
		double temp;
		temp = list.get(index1).getDistanceToNextPoint();
		temp = temp / interpolationValue;
		int n = (int) temp;
		// Distance for missing points
		float latdiff = list.get(index2).getLatitude() - list.get(index1).getLatitude();
		float longdiff = list.get(index2).getLongitude() - list.get(index1).getLongitude();
		latdiff = (float) (latdiff / n);
		longdiff = (float) (longdiff / n);
		double distanceInM = GeoDesy.distanceCalculationGEODESY(latdiff, longdiff, 0, 0);
		// Calculate time difference
		long timediff = list.get(index1).getTimediffToNextPoint();
		timediff = (long) (timediff / n);
		// Generate & add points
		list.get(index1).setDistanceToNextPoint(distanceInM);
		list.get(index1).setTimediffToNextPoint(timediff);
		for (int i = 1; i <= n - 1; i++) {
			GPS_plus gps = new GPS_plus(list.get(index1).getLatitude() + (latdiff * i),
					list.get(index1).getLongitude() + (longdiff * i),
					new Date(list.get(index1).getTime().getTime() + (timediff * i)), list.get(index1).getUserID());
			gps.setDistanceToNextPoint(distanceInM);
			gps.setTimediffToNextPoint(timediff);
			gps.setHead(list.get(index1).getHead());
			list.add(index1 + i, gps);
		}
		route.setTrajectory(list);
		return route;
	}

	/**
	 * Fills missing segments
	 * 
	 * @param route
	 *            :Route to fill Segments
	 * @param index1
	 *            :'start-index' of the missing segment
	 * @param index2
	 *            :'end-index' of the missing segment
	 * @param k
	 *            :defines how much points should influence the generation of
	 *            the new points
	 * @return processed Route
	 */
	static private Route fillMissingSegments(Route route, int index1, int index2, int k) {
		ArrayList<GPS_plus> list = route.getTrajectory();
		// Calculate N
		double erg1 = 0;
		double erg2 = 0;
		double erg3;
		erg3 = 2 * k * (Math.sqrt(Math.pow(calcDistance(list.get(index1), list.get(index2)), 2)));
		int leftborder = index1 - k;
		int rightborder = index2 + k - 1;
		if (leftborder < 0) {
			leftborder = 0;
		}
		if (rightborder >= list.size()) {
			rightborder = list.size() - 1;
		}
		for (int i = (leftborder); i < (index1 - 1); i++) {
			double temp;
			temp = list.get(i).getDistanceToNextPoint();
			temp = Math.pow(temp, 2);
			temp = Math.sqrt(temp);
			erg1 = erg1 + temp;

		}
		for (int i = (index2); i < (rightborder); i++) {
			double temp;
			temp = list.get(i).getDistanceToNextPoint();
			temp = Math.pow(temp, 2);
			temp = Math.sqrt(temp);
			erg2 = erg2 + temp;
		}
		double n = (erg3) / (erg1 + erg2);
		n = Math.floor(n);

		// Distance for missing points
		float latdiff = list.get(index2).getLatitude() - list.get(index1).getLatitude();
		float longdiff = list.get(index2).getLongitude() - list.get(index1).getLongitude();
		latdiff = (float) (latdiff / n);
		longdiff = (float) (longdiff / n);
		// Calculate timediff
		long timediff = list.get(index1).getTimediffToNextPoint();
		timediff = (long) (timediff / n);
		double distanceInM = GeoDesy.distanceCalculationGEODESY(latdiff, longdiff, 0, 0);
		list.get(index1).setDistanceToNextPoint(distanceInM);
		list.get(index1).setTimediffToNextPoint(timediff);
		// Generate & add points
		for (int i = 1; i < n - 1; i++) {
			GPS_plus gps = new GPS_plus(list.get(index1).getLatitude() + (latdiff * i),
					list.get(index1).getLongitude() + (longdiff * i),
					new Date(list.get(index1).getTime().getTime() + (timediff * i)), list.get(index1).getUserID());
			gps.setTimediffToNextPoint(timediff);
			gps.setDistanceToNextPoint(distanceInM);
			gps.setHead(list.get(index1).getHead());
			gps.setSpeed(list.get(index1).getSpeed());
			list.add(index1 + i, gps);
		}
		route.setTrajectory(list);
		return route;
	}

	/**
	 * Deletes erroneous data points out of a trajectory
	 * 
	 * @param trajectory
	 *            Input-Trajectory
	 * @return processed Trajectory
	 */
	static private Trajectory deleteErrors(Trajectory trajectory) {
		ArrayList<GPS_plus> list = trajectory.getTrajectory();

		// Erroneous data with Speed > thresholdMaxSpeed or Distance <
		// thresholdMinDistance or timespan < thresholdMinTimespan
		for (int i = 0; i < list.size() - 1;) {

			float lati = list.get(i).getLatitude();
			float longi = list.get(i).getLongitude();

			// check if the data point is still in the boundaries defined by our
			// grid over GPS data
			if ((lati < Grid.getMinLat()) || (Grid.getMaxLat() < lati) || (longi < Grid.getMinLong())
					|| (Grid.getMaxLong() < longi)) {
				list.remove(i);
				// if the data point is within the biundaries, check for errors
			} else {
				double distanceDiff = calcDistance(list.get(i), list.get(i + 1));
				long timeDiff = timeDiff(list.get(i + 1), list.get(i));
				double heading = calcHeading(list.get(i), list.get(i + 1));
				double timeInS = timeDiff / 1000;
				double speedinMperS = distanceDiff / timeInS;
				list.get(i).setDistanceToNextPoint(distanceDiff);
				list.get(i).setTimediffToNextPoint(timeDiff);
				list.get(i).setHead(heading);
				list.get(i).setSpeed(speedinMperS);

				if (speedinMperS > thresholdMaxSpeed) {
					list.remove(i + 1);
					// System.out.println("remove: speed");
				} else if (distanceDiff <= thresholdMinDistance) {
					list.remove(i + 1);
					// System.out.println("remove: distance");
				} else if (timeInS <= thresholdMinTimespan) {
					list.remove(i + 1);
					// System.out.println("remove: time");
				} else {
					i++;
				}
			}
		}
		float lati = list.get(list.size() - 1).getLatitude();
		float longi = list.get(list.size() - 1).getLongitude();
		if (list.size() > 1) {
			list.get(list.size() - 1).setHead(list.get(list.size() - 2).getHead());
		}
		if ((lati < Grid.getMinLat()) || (Grid.getMaxLat() < lati) || (longi < Grid.getMinLong())
				|| (Grid.getMaxLong() < longi)) {
			list.remove(list.size() - 1);
		}
		// return route
		trajectory.setTrajectory(list);
		return trajectory;
	}

	/**
	 * Method to smooth the route data
	 * 
	 * @param route
	 *            :Route that is going to be smoothed
	 * @return processed Route
	 */
	static private Route smoothTrajectory(Route route) {
		ArrayList<GPS_plus> trajectory = route.getTrajectory();
		for (int i = 1; i < (trajectory.size() - 1); i++) {
			GPS_plus point = trajectory.get(i);
			GPS_plus predecessor = trajectory.get(i - 1);
			GPS_plus successor = trajectory.get(i + 1);
			point.setLatitude((float) ((point.getLatitude() * 0.6) + (predecessor.getLatitude() * 0.2)
					+ (successor.getLatitude() * 0.2)));
			point.setLongitude((float) ((point.getLongitude() * 0.6) + (predecessor.getLongitude() * 0.2)
					+ (successor.getLongitude() * 0.2)));
			point.setHead(((point.getHead() * 0.7) + (predecessor.getHead() * 0.1) + (successor.getHead() * 0.2)));
		}
		return route;
	}

	/**
	 * Checks if the route contains enough data points
	 * 
	 * @param route
	 *            :Route that is going to be checked
	 * @return true if the route is valid, false else
	 */
	@SuppressWarnings("unused")
	static private boolean checkForVaildRoute(Route route) {
		if (route.getTrajectory().size() < thresholdMinDatapoints) {
			return false;
		}
		return true;
	}

	/**
	 * Calculates the distance between two GPS-points
	 * 
	 * @param start
	 *            :GPS_plus point
	 * @param end
	 *            :GPS_plus point
	 * @return distance in double
	 */
	static public double calcDistance(GPS_plus start, GPS_plus end) {
		double distanceInM = GeoDesy.distanceCalculationGEODESY(start.getLatitude(), start.getLongitude(),
				end.getLatitude(), end.getLongitude());
		return distanceInM;
	}

	static public double calcDistance(float startLatitude, float startLongitude, float endLatitude, float endLongitude) {
		double distanceInM = GeoDesy.distanceCalculationGEODESY(startLatitude, startLongitude,
				endLatitude, endLongitude);
		return distanceInM;
	}

	/**
	 * Calculates the heading direction of a GPS point
	 * 
	 * @param start
	 *            :GPS_plus object, which is going to be attached by the heading
	 * @param end
	 *            :Sequential GPS_plus object
	 * @return double: heading for the start-point
	 */
	static private double calcHeading(GPS_plus start, GPS_plus end) {
		double heading = GeoDesy.headCalculationGEODESY(start.getLatitude(), start.getLongitude(), end.getLatitude(),
				end.getLongitude());
		return heading;
	}

	/**
	 * Calculates the time difference between two GPS-points
	 * 
	 * @param p2
	 *            :GPS_plus point
	 * @param p1
	 *            :GPS_plus point
	 * @return long: (p2 - p1) time difference
	 */
	static private long timeDiff(GPS_plus p2, GPS_plus p1) {
		return p2.getTime().getTime() - p1.getTime().getTime();
	}
}
