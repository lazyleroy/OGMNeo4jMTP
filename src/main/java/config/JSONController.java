package config;

/**
 * Created by Felix Hambrecht on 17.07.2016.
 */

import EntityWrappers.*;
import entities.*;
import LocationProcessorServer.datastructures.Route;
import LocationProcessorServer.spotMapping.SpotHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Goodybag;
import entities.Spot;
import entities.User;
import org.neo4j.ogm.model.Result;
import org.springframework.web.bind.annotation.*;
import requestAnswers.LoginAnswer;
import requestAnswers.RegisterAnswer;
import requestAnswers.SimpleAnswer;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds all RESTful WebService methods. They are called via the Request Mappings. Each method of this class will
 * automatically call its corresponding method in the DatabaseOperations class
 */

@RestController
public class JSONController {


    private Neo4jGraphController neo4jGraphController = new Neo4jGraphController();



    SpotHandler spotHandler = new SpotHandler();

    /**
     * This method receives a Route of the Client in JSON-Format, afterwards it
     * directly processes it
     *
     * @param jsonRoute
     *            :Route in JSON-format
     * @return String as response to the client
     */

    @RequestMapping(value="/post/singleRoute", method = RequestMethod.POST)
    public String getRouteInJSON(@RequestBody String jsonRoute) {
        long startTime = System.currentTimeMillis();

        ObjectMapper mapper = new ObjectMapper();
        Route route = null;
        try {
            route = mapper.readValue(jsonRoute, Route.class);
        } catch (Exception e) {

            CharArrayWriter cw = new CharArrayWriter();
            PrintWriter w = new PrintWriter(cw);
            e.printStackTrace(w);
            w.close();
            return cw.toString();
            //e.printStackTrace();
            //return "Route konnte nicht gelesen werden";
        }

        // map into spots
        route = spotHandler.learningSpotStructure(route);
        //SystemData.getRoutes().add(route);
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
        //SystemData.getAbstractedBySpots().add(abstractedBySpots);

        //SystemData.getAbstractedByNodes().add(abstractedByNodes);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        return "Route processed";
    }
    /**
     * This method receives multiple Routes of the Client in JSON-Format,
     * afterwards it directly processes the routes.
     *
     * @param jsonRoutes
     *            :Routes in JSON-format
     * @return String as response to the client
     */
    @RequestMapping(value="/post/multipleRoutes", method = RequestMethod.POST)
    public String getRoutesInJSON(@RequestBody String jsonRoutes) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            ArrayList<Route> routes = mapper.readValue(jsonRoutes, new TypeReference<ArrayList<Route>>() {
            });

        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            // map into spots
            route = spotHandler.learningSpotStructure(route);
            //System.out.println(SystemData.getRoutes());
            //SystemData.getRoutes().add(route);
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
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Routes processed";
    }


    @RequestMapping(value="/sendQuery", method = RequestMethod.POST)
    public Result sendQuery(@RequestParam(value="query") String query){
        return neo4jGraphController.sendQuery(query);
    }

}
