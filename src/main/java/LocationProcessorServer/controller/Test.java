package LocationProcessorServer.controller;

import LocationProcessorServer.datastructures.Route;
import LocationProcessorServer.datastructures.Trajectory;
import LocationProcessorServer.gpxParser.GPXHandler;
import LocationProcessorServer.spotMapping.Grid;
import LocationProcessorServer.spotMapping.SpotHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.MyConfiguration;
import config.Neo4jGraphController;
import entities.GPS_plus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.SystemEnvironmentPropertySource;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Test class to start and test the program.
 * 
 * @author simon_000
 */
@EnableAutoConfiguration
@SpringBootApplication
public class Test {


	static SpotHandler spotHandler = new SpotHandler();
	static Neo4jGraphController neo4jGraphController = new Neo4jGraphController();

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

		neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(spotID)");
		neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(longitude)");
		neo4jGraphController.sendQuery("CREATE INDEX ON :Spot(latitude)");
		neo4jGraphController.sendQuery("CREATE INDEX ON :GPS_Plus(gpsPlusID)");
		neo4jGraphController.sendQuery("CREATE INDEX ON :Waypoint(waypointID)");
        neo4jGraphController.sendQuery("CALL spatial.addWKTLayer(\'SpotIndex\', \'wkt\')");

		// read data sets
		/*
		ArrayList<GPS_plus> testData = new ArrayList<GPS_plus>();
		Test.getTestdata(testData);
		Trajectory traTestData = new Trajectory("007");
		traTestData.setTrajectory(testData);
		ArrayList<GPS_plus> evaluationData = new ArrayList<>();
		evaluationData.addAll(testData);*/

		// clean data and search for routes in the input trajectories
        File dir = new File("src\\main\\resources\\asiaRoutes");
        File[] filesList = dir.listFiles();
		//ArrayList<Route> routes = getAsiaRoutes();
		//routes.addAll(GPSDataProcessor.splitTrajectoryByRoutes(traTestData));
        long counter = 0;
		// record time for performance
		Date start_time = new Date();
        for(int x = 0; x<5000; x++ ) {
                ArrayList<GPS_plus> gps_points = new ArrayList<GPS_plus>();
                gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\asiaRoutes\\"+filesList[x].getName()));
                for (int r = 0; r < gps_points.size(); r++) {
                    gps_points.get(r).setUserID("007");

                }
                Route route = new Route(gps_points, "007");


                        // routes.get(i);
                System.out.println("Route #:  " + x);
                System.out.println("Route Size:  " + route.size());
                counter += route.size();
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(route);
                    //System.out.println(json);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }



                route = spotHandler.learningSpotStructure(route);
            //}
        }
        // measure time for performance
		Date stop_time = new Date();
		double time = stop_time.getTime() - start_time.getTime();

		System.out.println("-- Evaluation --");


        System.out.println("# OF ROUTES PROCESSED 5000"+/*+ routes.size()+ */", NODES PROCESSED: "+ counter);
		System.out.println("Processing - Time: " + time );
		System.out.println("Neo4J Processing - Time " + SpotHandler.neo4jTimeOverall);
        System.out.println();

		System.out.println("-- Neo4J Evaluation --");
		System.out.println("\t AddSpot - Time: "+ SpotHandler.addSpotTimeOverall);
		System.out.println("\t Update Spots - Time: "+ SpotHandler.updateSpotTimeOverall);
		System.out.println("\t Get Spot - Time: "+SpotHandler.getSpotTimeOverall);
		System.out.println("\t Get Spots - Time: "+SpotHandler.getSpotsTimeOverall);
		System.out.println("\t Add GPS Points - Time: "+SpotHandler.addGPSPointsTimeOverall);
		System.out.println("\t Add GPS Points1 - Time: "+SpotHandler.addGPSPoints1TimeOverall);
		System.out.println("\t Add neighbours - Time: "+SpotHandler.addNeighbourTimeOverall);
		System.out.println("\t Set intersection - Time: "+SpotHandler.setIntersectionTimeOverall);
		System.out.println("\t Optimization Time :"+ SpotHandler.optimizationTimeOverall);
		System.out.println();

        System.out.println();
        System.out.println("\t AVERGAE AddSpot - Time PER NODE: "+ (double)SpotHandler.addSpotTimeOverall/counter);
        System.out.println("\t AVERGAE Update Spots - Time PER NODE: "+ (double)SpotHandler.updateSpotTimeOverall/counter);
        System.out.println("\t AVERGAE Get Spot - Time PER NODE: "+(double)SpotHandler.getSpotTimeOverall/counter);
        System.out.println("\t AVERGAE Get Spots - Time PER NODE: "+(double)SpotHandler.getSpotsTimeOverall/counter);
        System.out.println("\t AVERGAE Add GPS Points - Time PER NODE: "+(double)SpotHandler.addGPSPointsTimeOverall/counter);
        System.out.println("\t AVERGAE Add GPS Points1 - Time PER NODE: "+(double)SpotHandler.addGPSPoints1TimeOverall/counter);
        System.out.println("\t AVERGAE Add neighbours - Time PER NODE: "+(double)SpotHandler.addNeighbourTimeOverall/counter);
        System.out.println("\t AVERGAE Set intersection - Time PER NODE: "+(double)SpotHandler.setIntersectionTimeOverall/counter);
        System.out.println("\t AVERGAE Optimization Time PER NODE: "+ (double)(SpotHandler.optimizationTimeOverall/counter));
        System.out.println();



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
		for(int j = 1; j <= 16; j++) {

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
		for(int j = 1; j <= 3; j++) {

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

	public static ArrayList<Route> getAsiaRoutes(){

        ArrayList<Route> routes = new ArrayList<>();


        for(int i = 0; i < 5000; i++){
            System.out.println(i);
            File dir = new File("src\\main\\resources\\asiaRoutes");
            File[] filesList = dir.listFiles();
            ArrayList<GPS_plus> gps_points = new ArrayList<GPS_plus>();
            gps_points.addAll(GPXHandler.readGPXFile("src\\main\\resources\\asiaRoutes\\"+filesList[i].getName()));
            for (int r = 0; r < gps_points.size(); r++) {
                gps_points.get(r).setUserID("007");

            }
            Route route = new Route(gps_points, "007");
            routes.add(route);
        }

        return routes;
    }

}
