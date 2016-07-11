package entities;

import org.neo4j.ogm.annotation.NodeEntity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Felix on 11.07.2016.
 */
@NodeEntity
public class User extends BaseModel {

    private String userName;
    private String emailAdress;
    //////////
    private String salt;
    private String passWord;
    //////////
    //private int rating = 1;
    //private boolean isTrackingActivated = true;
    //private List<Route> routes = new ArrayList<Route>();
    //private GeoLocation location = new GeoLocation();

    public User(String userName, String emailAdress, String passWord){
        this.userName = userName;
        this.emailAdress = emailAdress;
        Date date = new Date();
        this.salt = Long.toString(date.getTime());
        this.passWord = createMD5(passWord, salt);
        System.out.println("MD5-Hash: "+this.passWord);

    }


    private String createMD5(String passWord, String salt){
        System.out.println("Passwort: " + passWord);
        System.out.println("Timestamp in Millisekunden: "+salt);
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



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAdress() {
        return emailAdress;
    }

    public void setEmailAdress(String emailAdress) {
        this.emailAdress = emailAdress;
    }

    public String getSalt(){return salt;}

    public void  setSalt(String salt){ this.salt = salt; }

  /* public int getRating() {
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

*/


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (userName == null) {
            if (other.userName != null)
                return false;
        } else if (!userName.equals(other.userName))
            return false;
        return true;
    }



}
