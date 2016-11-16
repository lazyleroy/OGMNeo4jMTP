package LocationProcessorServer.controller;

import LocationProcessorServer.datastructures.Route;
import LocationProcessorServer.datastructures.Trajectory;
import LocationProcessorServer.gpxParser.GPXHandler;
import LocationProcessorServer.spotMapping.Grid;
import LocationProcessorServer.spotMapping.SpotHandler;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;
import entities.GPS_plus;
import entities.Spot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller class to start and test the program.
 * 
 * @author simon_000
 */
public class Controller {

	static SpotHandler spotHandler = new SpotHandler();

	/**
	 * Main method to test the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// record time for performance
		Date start_time = new Date();

		// initialize
		SystemData.setRoutes(new ArrayList<Route>());
		SystemData.setAbstractedBySpots(new ArrayList<Route>());

		Grid.setMinLat(48);
		Grid.setMaxLat(52);
		Grid.setMinLong(6);
		Grid.setMaxLong(11);

		// read data sets
		ArrayList<GPS_plus> approximationData = new ArrayList<GPS_plus>();
		//Controller.getDataApprox(approximationData);
		Trajectory traApproximation = new Trajectory("UserX");
		traApproximation.setTrajectory(approximationData);

		ArrayList<GPS_plus> trainingData = new ArrayList<GPS_plus>();
		//Controller.getTrainingData(trainingData);
		Trajectory traTraining = new Trajectory("UserX");
		traTraining.setTrajectory(trainingData);

		ArrayList<GPS_plus> evaluationDataPart1 = new ArrayList<GPS_plus>();
		Controller.getEvaluationData1(evaluationDataPart1);
		Trajectory traEvaluationP1 = new Trajectory("UserY");
		traEvaluationP1.setTrajectory(evaluationDataPart1);

		ArrayList<GPS_plus> evaluationDataPart2 = new ArrayList<GPS_plus>();
		Controller.getEvaluationData2(evaluationDataPart2);
		Trajectory traEvaluationP2 = new Trajectory("UserX");
		traEvaluationP2.setTrajectory(evaluationDataPart2);
		
		ArrayList<GPS_plus> graphTest = new ArrayList<GPS_plus>();
		//Controller.getGraphTestData(graphTest);
		Trajectory traGraphtest = new Trajectory("UserX");
		traGraphtest.setTrajectory(graphTest);
		

		// clean data and search for routes in the input trajectories
		ArrayList<Route> routes = new ArrayList<Route>();
		//routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traGraphtest));
		routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traEvaluationP1));
		routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traEvaluationP2));
		//routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traApproximation));
		//routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traTraining));

		for (int i = 0; i < routes.size(); i++) {
			Route route = routes.get(i);
			// map into spots
			route = spotHandler.learningSpotStructure(route);
			SystemData.getRoutes().add(route);
			// abstract routes by spots
			Route abstractedBySpots = new Route(new ArrayList<GPS_plus>(), route.getUser());
			Spot lastSpot = null;
			for (int j = 0; j < route.size(); j++) {
				Spot spot = route.getTrajectory().get(j).getSpot();
				if (spot != lastSpot && spot != null) {
					abstractedBySpots.getTrajectory().add(route.getTrajectory().get(j));
					lastSpot = spot;
				}
			}
			SystemData.getAbstractedBySpots().add(abstractedBySpots);
		}

		// measure time for performance
		Date stop_time = new Date();
		double time = stop_time.getTime() - start_time.getTime();
		time = time / 1000;
		System.out.println("Processing-Time: " + time + " seconds");
	}

	/**
	 * Method to get data from GPX-files
	 * 
	 * @param gps_list
	 *            : target-list for GPS data
	 */
	public static void getDataApprox(ArrayList<GPS_plus> gps_list) {
		List<GPS_plus> gps_points = new ArrayList<GPS_plus>();
		for (int i = 1; i < 18; i++) {
			gps_points.addAll(GPXHandler.readGPXFile("Approximationtestdata/a1 (" + i + ").gpx"));
		}
		for (int i = 0; i < gps_points.size(); i++) {
			gps_points.get(i).setUserID("UserX");
			gps_list.add(gps_points.get(i));
		}
		System.out.println("Data read!");
	}

	/**
	 * Method to get data from GPX-files
	 * 
	 * @param gps_list
	 *            : target-list for GPS data
	 */
	public static void getTrainingData(ArrayList<GPS_plus> gps_list) {
		List<GPS_plus> gps_points = new ArrayList<GPS_plus>();
		for (int i = 1; i < 29; i++) {
			gps_points.addAll(GPXHandler.readGPXFile("Trainingdata/t" + i + ".gpx"));
		}
		
		for (int i = 0; i < gps_points.size(); i++) {
			gps_points.get(i).setUserID("UserX");
			gps_list.add(gps_points.get(i));
		}
		System.out.println("training # data points: " + gps_list.size());
	}
	

	/**
	 * Method to get data from GPX-files
	 * 
	 * @param gps_list
	 *            : target-list for GPS data
	 */
	public static void getEvaluationData1(ArrayList<GPS_plus> gps_list) {
		List<GPS_plus> gps_points = new ArrayList<GPS_plus>();
		for (int i = 1; i < 27; i++) {
			gps_points.addAll(GPXHandler.readGPXFile("Evaluationdata/r" + i + ".gpx"));
		}
		for (int i = 0; i < gps_points.size(); i++) {
			gps_points.get(i).setUserID("UserY");
			gps_list.add(gps_points.get(i));
		}
		System.out.println("set1 # data points: " + gps_list.size());
	}

	/**
	 * Method to get data from GPX-files
	 * 
	 * @param gps_list
	 *            : target-list for GPS data
	 */
	public static void getEvaluationData2(ArrayList<GPS_plus> gps_list) {
		List<GPS_plus> gps_points = new ArrayList<GPS_plus>();
		for (int i = 1; i < 52; i++) {
			gps_points.addAll(GPXHandler.readGPXFile("Evaluationdata/s" + i + ".gpx"));
		}
		for (int i = 0; i < gps_points.size(); i++) {
			gps_points.get(i).setUserID("UserX");
			gps_list.add(gps_points.get(i));
		}
		System.out.println("set2 # data points: " + gps_list.size());
	}

	
	/**
	 * Method to get data from GPX-files
	 * 
	 * @param gps_list
	 *            : target-list for GPS data
	 */
	public static void getGraphTestData(ArrayList<GPS_plus> gps_list) {
		List<GPS_plus> gps_points = new ArrayList<GPS_plus>();
		for (int i = 1; i < 8; i++) {
			gps_points.addAll(GPXHandler.readGPXFile("Graphtestdata/g1 ("+i+").gpx"));
		}
		
		for (int i = 0; i < gps_points.size(); i++) {
			gps_points.get(i).setUserID("UserX");
			gps_list.add(gps_points.get(i));
		}
		System.out.println("graphtest # data points: " + gps_list.size());
	}
}
