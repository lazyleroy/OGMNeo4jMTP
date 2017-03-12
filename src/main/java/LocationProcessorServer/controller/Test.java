package LocationProcessorServer.controller;

import LocationProcessorServer.datastructures.Route;
import LocationProcessorServer.datastructures.Trajectory;
import LocationProcessorServer.gpxParser.GPXHandler;
import LocationProcessorServer.spotMapping.Grid;
import LocationProcessorServer.spotMapping.SpotHandler;
import LocationProcessorServer.trajectoryPreparation.GPSDataProcessor;

import ch.qos.logback.core.CoreConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

		Grid.setMinLat(48);
		Grid.setMaxLat(52);
		Grid.setMinLong(6);
		Grid.setMaxLong(11);

		// read data sets
		/*
		ArrayList<GPS_plus> testData = new ArrayList<GPS_plus>();
		Test.getTestdata(testData);
		Trajectory traTestData = new Trajectory("007");
		traTestData.setTrajectory(testData);
		ArrayList<GPS_plus> evaluationData = new ArrayList<>();
		evaluationData.addAll(testData);*/

		// clean data and search for routes in the input trajectories
		ArrayList<Route> routes = getTestRoutesSimon();

		//routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traTestData));

		// record time for performance
		Date start_time = new Date();

		for (int i = 0; i < routes.size(); i++) {
			Route route = routes.get(i);
            try {
                ObjectMapper mapper = new ObjectMapper();
                System.out.println(mapper.writeValueAsString(route));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
			route = spotHandler.learningSpotStructure(route);
		}

        // measure time for performance
		Date stop_time = new Date();
		double time = stop_time.getTime() - start_time.getTime();
		time = time / 1000;

		System.out.println("-- Evaluation --");
		System.out.println("# of Testroutes: "+routes.size());
		for (int i = 0; i < routes.size(); i++) {
			Route route = routes.get(i);
			System.out.println("Route "+(i+1)+": "+route.size()+" points");
		}
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

	public static ArrayList<Route> getTestRoutes(){
		ArrayList<Route> routes = new ArrayList<>();
		for(int j = 1; j <= 10; j++) {

			ArrayList<GPS_plus> gps_points = new ArrayList<GPS_plus>();
			gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\Routen\\data ("+j+").gpx"));
			for (int i = 0; i < gps_points.size(); i++) {
				gps_points.get(i).setUserID("007");
			}
			Route route = new Route(gps_points, "007");
			routes.add(route);
		}

		return routes;
	}

	public static ArrayList<Route> getTestRoutesSimon(){
		ArrayList<Route> routes = new ArrayList<>();
		for(int j = 1; j <= 6; j++) {

			ArrayList<GPS_plus> gps_points = new ArrayList<GPS_plus>();
			gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\GPXfiles\\track ("+j+").gpx"));
			for (int i = 0; i < gps_points.size(); i++) {
				gps_points.get(i).setUserID("007");
			}
			Route route = new Route(gps_points, "007");
			routes.add(route);
		}

		return routes;
	}
}
