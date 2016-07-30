package entities;

import config.Main;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private String occupation;
    private int phone;
    private String salt;
    private String passWord;
    private static Main main = new Main();
    private int rating;
    private boolean isTrackingActivated;
    private GeoLocation location;
    private String profilePicture;

    @Relationship(type = "WALKS", direction = Relationship.OUTGOING)
    private List<Route> routes = new ArrayList<Route>();


    public User(String userName, String emailAddress, String passWord){
        this.userName = userName;
        this.emailAddress = emailAddress;
        Date date = new Date();
        this.salt = Long.toString(date.getTime());
        this.passWord = createMD5(passWord, this.salt);
        this.rating = 0;
        this.isTrackingActivated = false;
        this.profilePicture = "default";
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
    public GeoLocation getLocation() {
        return location;
    }
    public void setLocation(GeoLocation location) {
        this.location = location;
    }



    public String createMD5(String passWord, String salt){
        try {
            MessageDigest md5;
            md5 = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md5.digest((passWord+salt).getBytes());
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


    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
