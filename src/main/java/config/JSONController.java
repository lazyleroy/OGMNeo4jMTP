package config;

/**
 * Created by Felix Hambrecht on 17.07.2016.
 */

import EntityWrappers.GoodybagWrapper;
import LocationProcessorServer.controller.SystemData;
import LocationProcessorServer.datastructures.GPS_plus;
import LocationProcessorServer.datastructures.Route;
import LocationProcessorServer.datastructures.Spot;
import LocationProcessorServer.graphStructure.GraphHandler;
import LocationProcessorServer.graphStructure.Node;
import LocationProcessorServer.spotMapping.SpotHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Goodybag;
import entities.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import requestAnswers.LoginAnswer;
import requestAnswers.RegisterAnswer;
import requestAnswers.SimpleAnswer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds all RESTful WebService methods. They are called via the Request Mappings. Each method of this class will
 * automatically call its corresponding method in the DatabaseOperations class
 */

@RestController
public class JSONController {

    private DatabaseOperations db = new DatabaseOperations();

    @CrossOrigin(origins = "134.155.48.48:8080")
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public RegisterAnswer register(@RequestParam(value="username") String name,@RequestParam(value="email") String email,
                                   @RequestParam(value="password") String password,
                                   @RequestParam(value="firebaseToken", required = false)String firebaseToken){
        return db.register(new User(name, email,password), email, firebaseToken);
    }

    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    public SimpleAnswer updateProfile(@RequestParam(value="username", required = false)String userName,
                                      @RequestParam(value="email", required = false) String email,
                                      @RequestHeader(value = "accessToken") String accessToken){
        return db.updateProfile(userName, email, accessToken);
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginAnswer emailLogin(@RequestParam(value="email") String email, @RequestParam(value="password") String password,
                                  @RequestParam(value="firebaseToken", required = false)String firebaseToken) {
        return db.emailLogin(email, password, firebaseToken);
    }

    @RequestMapping(value = "/refreshTokenLogin", method = RequestMethod.POST)
    public RegisterAnswer refreshTokenLogin(@RequestParam(value="refreshToken")String refreshToken,
                                            @RequestParam(value ="clientID")int clientID,
                                            @RequestParam (value = "clientSecret") String clientSecret){
        return db.refreshTokenLogin(refreshToken, clientID, clientSecret);
    }

    @RequestMapping(value="/uploadGoodybag", method = RequestMethod.POST)
    public SimpleAnswer uploadGoodybag(
        @RequestBody GoodybagWrapper params, @RequestHeader(value = "accessToken") String accessToken){

        return db.uploadGoodybag(params.getTitle(), params.getStatus(), params.getDescription(), params.getTip(),
                params.getDeliverTime(), params.getDeliverLocation(), params.getShopLocation(),params.getCheckOne(),
                params.getCheckTwo(), accessToken);
    }



    @RequestMapping(value="/changePassword", method = RequestMethod.POST)
    public SimpleAnswer changePassword(@RequestParam(value="oldPassword")String oldPassword,
                                       @RequestParam(value="newPassword")String newPassword,
                                       @RequestHeader(value="accessToken")String accessToken
                                       ){
        return db.changePassword(oldPassword,newPassword, accessToken);
    }

    @RequestMapping(value="/storeFirebaseToken", method = RequestMethod.POST)
    public SimpleAnswer storeFirebaseToken(@RequestHeader(value="refreshToken")String refreshToken,
                                           @RequestParam(value="firebaseToken") String firebaseToken){
        return db.storeFirebaseToken(refreshToken, firebaseToken);
    }


    @RequestMapping(value="/finishGoodybag", method = RequestMethod.POST)
    public SimpleAnswer finishGoodybag(@RequestHeader(value="accessToken") String accessToken,
                                       @RequestParam(value ="goodybagID")String goodybagID, @RequestParam(value="rating")int rating,
                                       @RequestParam(value = "creatorRates")boolean creatorRates){
        return db.finishGoodybag(goodybagID, rating, creatorRates, accessToken);
    }

    @RequestMapping(value="/acceptGoodybag", method = RequestMethod.POST)
    public SimpleAnswer acceptGoody(@RequestHeader(value="accessToken") String accessToken, @RequestParam(value="goodybagID")String goodybagID){
        return db.acceptGoodybag(goodybagID, accessToken);
    }

    @RequestMapping(value="/myGoodybags", method = RequestMethod.POST)
    public @ResponseBody
    List<Goodybag> myGoodybags(@RequestHeader(value="accessToken")String accessToken){
        return db.myGoodybags(accessToken);
    }
    @RequestMapping(value = "/myMatchedGoodybags", method = RequestMethod.POST)
    public @ResponseBody List<Goodybag> myMatchedGoodybags(@RequestHeader(value="accessToken")String accessToken){
        return db.matchedGoodybags(accessToken);
    }

    @RequestMapping(value="/getGoodybagByID", method = RequestMethod.POST)
    public @ResponseBody Goodybag getGoodybagByID(@RequestHeader(value ="accessToken")String accessToken,
                                                  @RequestParam(value="goodybagID")String goodybagID){
        return db.getGoodybagbyID(goodybagID, accessToken);
    }

    @RequestMapping(value="/numberOfFinishedGoodybags", method = RequestMethod.POST)
    public @ResponseBody int numberOfFinishedGoodybags(@RequestHeader(value="accessToken") String accessToken){
        return db.getNumberOfFinishedGoodybags(accessToken);
    }


    /**
     * This method receives a Route of the Client in JSON-Format, afterwards it
     * directly processes it
     *
     * @param jsonRoute
     *            :Route in JSON-format
     * @return String as response to the client
     */

    @RequestMapping(value="/post/singleRoute", method = RequestMethod.POST)
    public String getRouteInJSON(String jsonRoute) {

        ObjectMapper mapper = new ObjectMapper();
        Route route = null;
        try {
            route = mapper.readValue(jsonRoute, Route.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // map into spots
        route = SpotHandler.learningSpotStructure(route);
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

        // maintain graph structue
        GraphHandler.updateGraph(route);
        // abstract by nodes
        Route abstractedByNodes = new Route(new ArrayList<GPS_plus>(), route.getUser());
        Node lastNode = null;
        for (int j = 0; j < route.size(); j++) {
            Node node = route.getTrajectory().get(j).getSpot().node;
            if (node != lastNode && node != null) {
                abstractedByNodes.getTrajectory().add(route.getTrajectory().get(j));
                lastNode = node;
            }
        }
        SystemData.getAbstractedByNodes().add(abstractedByNodes);
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
    public String getRoutesInJSON(String jsonRoutes) {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<Route> routes = null;
        try {
            routes = mapper.readValue(jsonRoutes, new TypeReference<ArrayList<Route>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            // map into spots
            route = SpotHandler.learningSpotStructure(route);
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

            // maintain graph structue
            GraphHandler.updateGraph(route);
            // abstract by nodes
            Route abstractedByNodes = new Route(new ArrayList<GPS_plus>(), route.getUser());
            Node lastNode = null;
            for (int j = 0; j < route.size(); j++) {
                Node node = route.getTrajectory().get(j).getSpot().node;
                if (node != lastNode && node != null) {
                    abstractedByNodes.getTrajectory().add(route.getTrajectory().get(j));
                    lastNode = node;
                }
            }
            SystemData.getAbstractedByNodes().add(abstractedByNodes);
        }
        return "Routes processed";
    }







}
