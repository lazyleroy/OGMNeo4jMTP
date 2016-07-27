package config; /**
 * Created by Felix on 17.07.2016.
 */

import EntityWrappers.GoodybagWrapper;
import EntityWrappers.RouteWrapper;
import config.DatabaseOperations;
import requestAnswers.RegisterAnswer;
import entities.User;
import org.springframework.web.bind.annotation.*;
import requestAnswers.SimpleAnswer;

@RestController
public class JSONController {

    private DatabaseOperations db = new DatabaseOperations();

    @CrossOrigin(origins = "134.155.48.48:8080")
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public RegisterAnswer register(@RequestParam(value="username") String name,@RequestParam(value="email") String email, @RequestParam(value="password") String password) {
        return db.register(new User(name, email,password), email);
    }

    @RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
    public SimpleAnswer updateProfile(@RequestParam(value="username")String userName, @RequestParam(value="email")String email,
                                      @RequestParam(value="occupation") String occupation, @RequestParam(value = "phone")int phone,
                                      @RequestParam(value="accessToken") String accessToken){
        return db.updateProfile(userName, email, occupation, accessToken);
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public RegisterAnswer register(@RequestParam(value="email") String email, @RequestParam(value="password") String password) {
        return db.emailLogin(email, password);
    }

    @RequestMapping(value="/uploadGoodybag", method = RequestMethod.POST)
    public SimpleAnswer uploadGoodybag(
        @RequestBody GoodybagWrapper params){
        return db.uploadGoodybag(params.getCreatorName(), params.getCreatorImage(), params.getTitle(),
                params.getStatus(), params.getDescription(), params.getTip(), params.getCreationTime(),
                params.getDeliverTime(), params.getDeliverLocation(), params.getShopLocation(), params.getAccessToken());
    }

    @RequestMapping(value="/uploadRoute", method = RequestMethod.POST)
    public SimpleAnswer uploadRoute(@RequestBody RouteWrapper route){
        for(int i = 0; i < route.getRoute().size(); i++){
            route.getRoute().get(i).setGeoLocationID(i);
        }
        return db.uploadRoute(route.getRoute(), route.getAccessToken());
    }



    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String test(@RequestBody GoodybagWrapper test){

        return null;
    }
}
