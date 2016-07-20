package jsonTests; /**
 * Created by Felix on 17.07.2016.
 */

import config.DatabaseOperations;
import requestAnswers.RegisterAnswer;
import entities.User;
import org.springframework.web.bind.annotation.*;
import requestAnswers.SimpleAnswer;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private DatabaseOperations db = new DatabaseOperations();


    @CrossOrigin(origins = "192.168.0.101:8080")
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public RegisterAnswer register(@RequestParam(value="username") String name,@RequestParam(value="email") String email, @RequestParam(value="password") String password) {
        return db.register(new User(name, email,password), email);
    }
    @RequestMapping(value = "/updateProfile", method = RequestMethod.GET)
    public SimpleAnswer updateProfile(@RequestParam(value="username")String userName, @RequestParam(value="email")String email,
                                      @RequestParam(value="occupation") String occupation,
                                      @RequestParam(value="accessToken") String accessToken){
        return db.updateProfile(userName, email, occupation,accessToken);
    }
}
