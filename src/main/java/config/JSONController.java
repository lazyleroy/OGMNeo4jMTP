package config; /**
 * Created by Felix on 17.07.2016.
 */

import EntityWrappers.GoodybagWrapper;
import EntityWrappers.RouteWrapper;
import entities.Goodybag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import requestAnswers.LoginAnswer;
import requestAnswers.RegisterAnswer;
import entities.User;
import org.springframework.web.bind.annotation.*;
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

    /*@RequestMapping(value="/uploadRoute", method = RequestMethod.POST)
    public SimpleAnswer uploadRoute(

            @RequestBody RouteWrapper route, @RequestHeader(value = "accessToken") String accessToken){
        return db.uploadRoute(route.getRoute(), accessToken);
    }*/

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


    /*@RequestMapping(value = "/sendGoodybagToUsers", method = RequestMethod.POST)
    public void sendGoodybagToUsers(@RequestParam (value = "userIDs")ArrayList<Long>userIDs, @RequestParam(value="goodyBagID")String goodybagID){
        db.sendGoodybagToUsers(userIDs,goodybagID);
    }*/

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
}
