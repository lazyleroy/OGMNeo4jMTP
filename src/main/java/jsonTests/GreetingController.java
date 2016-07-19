package jsonTests; /**
 * Created by Felix on 17.07.2016.
 */

import com.sun.javafx.collections.MappingChange;
import config.DatabaseOperations;
import entities.User;
import org.springframework.boot.test.SpringBootMockServletContext;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private DatabaseOperations db = new DatabaseOperations();


    @CrossOrigin(origins = "192.168.2.13:8080")
    @RequestMapping(value = "/register", params = {"name","email", "password"},method = RequestMethod.GET)
    public String register(String name, String email, String password
    ) {
        return db.register(new User(name, email,password), email);
        //return new User(name, email, password);
    }
}
