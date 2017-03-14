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

    private DatabaseOperations db = new DatabaseOperations();
    private Neo4jGraphController neo4jGraphController = new Neo4jGraphController();

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


    @RequestMapping(value="/saveDataEntity", method = RequestMethod.POST)
    public void saveDataEntity(@RequestParam(value="username")String username, @RequestParam(value="longitude")double longitude,
                               @RequestParam(value="latitude")double latitude, @RequestParam(value = "acceleration")double acceleration,
                               @RequestParam(value="volume")double volume, @RequestParam(value="track")long track,
                               @RequestParam(value="time")long time){
        db.saveDataEntity(username, longitude, latitude, acceleration, volume, track, time);
    }

    @RequestMapping(value="/sendQuery", method = RequestMethod.POST)
    public Result sendQuery(@RequestParam(value="query") String query){
        return neo4jGraphController.sendQuery(query);
    }

}
