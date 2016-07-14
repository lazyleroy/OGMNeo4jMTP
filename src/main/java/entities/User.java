package entities;

import config.Main;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.neo4j.template.Neo4jTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Felix on 11.07.2016.
 */
@NodeEntity
public class User extends BaseModel {

    private String userName;
    private String emailAddress;
    //////////
    private String salt;
    private String passWord;
    private static Main main = new Main();
    //////////
    private int rating;
    private boolean isTrackingActivated;

    @Relationship(type = "WALKS", direction = Relationship.OUTGOING)
    private List<Route> routes = new ArrayList<Route>();

    private GeoLocation location = new GeoLocation();

    public User(String userName, String emailAddress, String passWord){
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.passWord = createMD5(passWord);
    }
    public User(){
    super();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSalt(){return salt;}

    public void  setSalt(String salt){ this.salt = salt; }

    public String getPassword(){return passWord; }

    public void setPassword(String passWord){this.passWord = passWord;}

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isTrackingActivated() {
        return isTrackingActivated;
    }

    public void setIsTrackingActivated(boolean isTrackingActivated) {
        this.isTrackingActivated = isTrackingActivated;
    }
    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }


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
        u.setPassword(u.createMD5(passWord));
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







    private String createMD5(String passWord){
        try {
            Date date = new Date();
            this.salt = Long.toString(date.getTime());
            MessageDigest md5;
            md5 = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md5.digest((passWord+this.salt).getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException nsae){
            nsae.printStackTrace();
        }
        return null;
    }


}
