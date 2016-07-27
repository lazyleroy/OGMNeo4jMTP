package config;

import entities.*;
import requestAnswers.RegisterAnswer;
import entities.UserSession;
import org.neo4j.ogm.exception.NotFoundException;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import requestAnswers.SimpleAnswer;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Felix on 15.07.2016.
 */
public class DatabaseOperations {

    private static Main main = new Main();

    public static RegisterAnswer register(User u, String emailAddress){
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
        User t = template.loadByProperty(User.class, "emailAddress", emailAddress);
            template.purgeSession();
            template.clear();
            return new RegisterAnswer(false, "Emailadress already exists");
        }catch(NotFoundException nfe){
                UserSession uS = new UserSession(u);
                Cookie c = new Cookie(u);
                template.save(uS);
                template.save(c);
                String accessToken = uS.getAccessToken();
                String refreshToken = c.getRefreshToken();
            return new RegisterAnswer(true, accessToken, 86400000L, refreshToken, 15768000000L);
            }
    }

    public static RegisterAnswer checkAccessToken(String accesstoken) {
        long timestamp = new Date().getTime();
        Neo4jTemplate template = main.createNeo4JTemplate();
        try{
            UserSession u = template.loadByProperty(UserSession.class, "accessToken", accesstoken);
            if (u.getExpiresAt() >= timestamp) {
                return new RegisterAnswer(true);
        }   else {
                return new RegisterAnswer(false, "accessToken expired, refreshTokenLogin required");
            }
        }catch (NotFoundException nfe){
            return new RegisterAnswer(false, "Invalid accessToken, refreshTokenLogin required");
        }
    }

    public static RegisterAnswer emailLogin(String email, String password){
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            User u = template.loadByProperty(User.class,"emailAddress", email);
            String hash = u.createMD5(password, u.getSalt());
            if (hash.equals(u.getPassword())){
                UserSession uS = new UserSession(u);
                Cookie c = new Cookie(u);
                template.save(uS);
                template.save(c);
                return new RegisterAnswer(true,uS.getAccessToken(), 86400000L, c.getRefreshToken(), 15768000000L);
            }else {
                template.save(u);
                return new RegisterAnswer(false, "Invalid password");
                }
        }catch (NotFoundException nfe){
            return new RegisterAnswer(false, "Email does not exist.");
        }
    }

    public static RegisterAnswer refreshTokenLogin(String refreshToken, String clientID, String clientSecret) {
        long timestamp = new Date().getTime();
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            Cookie c = template.loadByProperty(Cookie.class, "refreshToken", refreshToken);
            if (c.getExpiresAt()  >= timestamp) {
                UserSession uS = new UserSession(c.getUser());
                template.save(c);
                return new RegisterAnswer(true, uS.getAccessToken(),86400000L, c.getRefreshToken(), 15768000000L);
            } else {
                template.save(c);
                return new RegisterAnswer(false, "refreshToken expired, emailLogin required");
            }
        } catch (NotFoundException nfe) {
                return new RegisterAnswer(false, "Invalid refreshToken, emailLogin required");
        }
    }

    public static SimpleAnswer updateProfile(String userName, String email, String occupation, String accessToken){
        if(checkAccessToken(accessToken).getSuccess()){
            Neo4jTemplate template = main.createNeo4JTemplate();
            try{
                UserSession uS = template.loadByProperty(UserSession.class, "accessToken", accessToken);
                User u = uS.getUser();
                    u.setUserName(userName);
                    u.setEmailAddress(email);
                    u.setOccupation(occupation);
                template.save(u);
                return new SimpleAnswer(true, "values updated: " + userName + " " + email + " " + " "+ occupation);
            }catch (NotFoundException nfe){
                return new SimpleAnswer(false, "Invalid Accesstoken");
            }
        }else return new SimpleAnswer(false, "Invalid Accesstoken");

    }


    public SimpleAnswer uploadGoodybag(String creatorName, int creatorImage, String title, String status, String description,
                                       double tip, long creationTime, long deliverTime, GeoLocation deliverLocation,
                                       GeoLocation shopLocation, String accessToken) {
        if(checkAccessToken(accessToken).getSuccess()){
            Neo4jTemplate template = main.createNeo4JTemplate();
            try{
                UserSession uS = template.loadByProperty(UserSession.class,"accessToken", accessToken);
                Goodybag gB = new Goodybag(creatorName, creatorImage, title, status, description, tip, creationTime, deliverTime,
                        deliverLocation, shopLocation, uS.getUser());
                template.save(gB);
                return new SimpleAnswer(true,gB.getGoodyBagID());
            }catch(NotFoundException nfe) {
                return new SimpleAnswer(false, "Invalid Accesstoken");
            }
        }else return new SimpleAnswer(false, "Invalid Accesstoken");
    }

    public SimpleAnswer uploadRoute(ArrayList<GeoLocation> routes, String accessToken){
        if(checkAccessToken(accessToken).getSuccess()){
            Neo4jTemplate template = main.createNeo4JTemplate();
            try {
                UserSession uS = template.loadByProperty(UserSession.class,"accessToken", accessToken);
                Route r = new Route(routes, uS.getUser());
                template.save(r);
                return  new SimpleAnswer(true, r.getRouteID());
            }catch(NotFoundException nfe){
                return new SimpleAnswer(false, "Invalid Accesstoken");

            }
        }else return new SimpleAnswer(false, "Invalid Accesstoken");
    }

}
