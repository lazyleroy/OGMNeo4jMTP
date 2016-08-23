package entities;

import config.Main;
import org.apache.commons.codec.digest.DigestUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
//import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
//import org.bouncycastle.jcajce.provider.digest.SHA3.Digest256;
import java.util.*;

/**
 * Created by Felix on 11.07.2016.
 */
@NodeEntity
public class User extends BaseModel {

    private String userName;
    private String emailAddress;
    private String salt;
    private String passWord;
    private static Main main = new Main();
    private double rating;
    private int cumulatedRatings;
    private boolean isTrackingActivated;
    private GeoLocation location;
    private String profilePicture;
    private long userID;
    private int numberOfRatings;
    private ArrayList<GeoLocation>startedAt;

    @Relationship(type = "WALKS", direction = Relationship.UNDIRECTED)
    private List<Route> routes = new ArrayList<Route>();

    @Relationship(type = "OWNS", direction = Relationship.OUTGOING)
    private ArrayList<Goodybag> goodybags = new ArrayList<Goodybag>();


    public User(String userName, String emailAddress, String passWord){
        this.userName = userName;
        this.emailAddress = emailAddress;
        Date d = new Date();
        this.salt = Long.toString(d.getTime());
        this.passWord = createSHA1(passWord, this.salt);
        this.rating = 0;
        this.isTrackingActivated = true;
        this.profilePicture = "default";
        this.numberOfRatings = 0;
        changeID();
    }

    public void changeID(){
        Random r = new Random();
        String c = "";
        for (int i = 1; i<17; i++){
            int rv = r.nextInt(10);
            if(i == 1){
                rv = r.nextInt(9);
            }
            c+= rv;
        }
        this.userID = Long.parseLong(c);
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
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
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
    public GeoLocation getLocation() {
        return location;
    }
    public void setLocation(GeoLocation location) {
        this.location = location;
    }



    public String createSHA1(String passWord, String salt){
        return DigestUtils.sha1Hex(passWord+salt);
    }


    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public long getUserID() {
        return userID;
    }

    public ArrayList<Goodybag> getGoodybags() {
        return goodybags;
    }

    public ArrayList<GeoLocation> getStartedAt() {
        return startedAt;
    }
    public void setStartedAt(ArrayList<GeoLocation> startedAt){
        this.startedAt = startedAt;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public int getCumulatedRatings() {
        return cumulatedRatings;
    }

    public void setCumulatedRatings(int cumulatedRatings) {
        this.cumulatedRatings = cumulatedRatings;
    }


}
