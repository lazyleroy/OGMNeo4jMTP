package LocationProcessorServer.controller;

import LocationProcessorServer.datastructures.Route;
import LocationProcessorServer.datastructures.Trajectory;
import LocationProcessorServer.gpxParser.GPXHandler;
import LocationProcessorServer.spotMapping.Grid;
import LocationProcessorServer.spotMapping.SpotHandler;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;
import config.MyConfiguration;
import entities.GPS_plus;
import entities.Spot;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Test class to start and test the program.
 * 
 * @author simon_000
 */
@SpringBootApplication
public class Test {
	public static SessionFactory sessionFactory = new MyConfiguration().getSessionFactory();
	static SpotHandler spotHandler = new SpotHandler();
	/**
	 * Main method to test the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		SpringApplication.run(Test.class, args);
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
		ArrayList<GPS_plus> testData = new ArrayList<GPS_plus>();
		Test.getTestdata(testData);
		Trajectory traTestData = new Trajectory("007");
		traTestData.setTrajectory(testData);

		// clean data and search for routes in the input trajectories
		ArrayList<Route> routes = new ArrayList<Route>();
		routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traTestData));

		//System.out.println(routes.size());

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
	public static void getTestdata(ArrayList<GPS_plus> gps_list) {
		List<GPS_plus> gps_points = new ArrayList<GPS_plus>();
		gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\GPXfiles\\track1.gpx"));
		gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\GPXfiles\\track2.gpx"));
		gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\GPXfiles\\track3.gpx"));
		for (int i = 0; i < gps_points.size(); i++) {
			gps_points.get(i).setUserID("007");
			gps_list.add(gps_points.get(i));
		}
	}
}
