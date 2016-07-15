package config;

import entities.Cookie;
import entities.User;
import entities.UserSession;
import org.neo4j.ogm.exception.NotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.util.Date;

/**
 * Created by Felix on 15.07.2016.
 */
public class DatabaseOperations {

    private static Main main = new Main();

    //Operations concerning the User
    public static String loadUserName(String accesstoken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        template.purgeSession();
        template.clear();
        return u.getUserName();
    }
    public static String loadEmailAddress(String accesstoken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        template.purgeSession();
        template.clear();
        return u.getEmailAddress();
    }
    public static String loadSalt(String accesstoken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        template.purgeSession();
        template.clear();
        return u.getSalt();
    }
    public static int loadRating(String accesstoken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        template.purgeSession();
        template.clear();
        return u.getRating();
    }
    public static String loadPassWord(String accesstoken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        template.purgeSession();
        template.clear();
        return u.getPassword();
    }
    public static boolean loadIsTrackingActivated(String accesstoken){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        template.purgeSession();
        template.clear();
        return u.isTrackingActivated();
    }
    public static void saveUserName(String accesstoken, String userName){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        u.setUserName(userName);
        template.save(u);
        template.purgeSession();
        template.clear();
    }
    public static void saveEmailAddress(String accesstoken, String emailAddress){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        u.setEmailAddress(emailAddress);
        template.save(u);
        template.purgeSession();
        template.clear();
    }
    public static void savePassword(String accesstoken, String passWord){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        u.setPassword(u.createMD5(passWord, u.getSalt()));
        template.save(u);
        template.purgeSession();
        template.clear();
    }
    public static void saveRating(String accesstoken, int rating){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        u.setRating(rating);
        template.save(u);
        template.purgeSession();
        template.clear();
    }
    public static void saveIsTrackingActivated(String accesstoken, boolean isTrackingActivated){
        Neo4jTemplate template = main.createNeo4JTemplate();
        User u = template.loadByProperty(User.class,"userName", accesstoken);
        u.setIsTrackingActivated(isTrackingActivated);
        template.save(u);
        template.purgeSession();
        template.clear();
    }

    //Operations concerning the UserSession

    public static String checkAccessToken(long accesstoken){
        long timestamp = new Date().getTime();
        Neo4jTemplate template = main.createNeo4JTemplate();
        UserSession u = template.loadByProperty(UserSession.class,"accessToken", accesstoken);
        if(u.getAccessToken()+86400000L>=timestamp){
            template.purgeSession();
            template.clear();
            return "success";
        }else {
            template.purgeSession();
            template.clear();
            return "failure";
        }
    }

    public static String emailLogin(String email, String password){
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            User u = template.loadByProperty(User.class,"emailAddress", email);

            String hash = u.createMD5(password, u.getSalt());
            if (hash.equals(u.getPassword())){
                u.getUserSessions().add(new UserSession(u));
                u.getCookies().add(new Cookie(u));
                template.save(u);
                template.purgeSession();
                template.clear();
                return "success";
            }else {
                template.save(u);
                template.purgeSession();
                template.clear();
                return "failure";
                }
        }catch (NotFoundException nfe){
                System.out.println("SOMETHING WENT WRONG !! NotFoundException !! MAILADDRESS !!");
            }
        return "failure";
    }

    public static String refreshTokenLogin(long refreshToken) {
        long timestamp = new Date().getTime();
        Neo4jTemplate template = main.createNeo4JTemplate();
        try {
            Cookie c = template.loadByProperty(Cookie.class, "refreshToken", refreshToken);
            if (refreshToken + 15768000000L >= timestamp) {
                c.getUser().getUserSessions().add(new UserSession(c.getUser()));
                template.save(c);
                template.purgeSession();
                template.clear();
                return "success";
            } else {
                template.save(c);
                template.purgeSession();
                template.clear();
                return "failure";
            }
        } catch (NotFoundException nfe) {
            System.out.println("SOMETHING WENT WRONG !! NotFoundException !! REFRESHTOKEN !!");
        }
        return "failure";
    }




}
