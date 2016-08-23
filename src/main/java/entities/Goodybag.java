package entities;

import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by Felix on 14.07.2016.
 */

public class Goodybag extends BaseModel{



    private String title;
    private String status;
    private String description;
    private double tip;
    private long creationTime;
    private long deliverTime;
    private GeoLocation deliverLocation;
    private GeoLocation shopLocation;
    private long goodybagID;
    private User user;

    @Relationship(type = "MATCHED_TO", direction = Relationship.OUTGOING)
    private ArrayList<User> matchedUsers = new ArrayList<>();

    public Goodybag(){
    }

    public Goodybag(String title, String status, String description,
                    double tip, long deliverTime, GeoLocation deliverLocation,
                    GeoLocation shopLocation, User user){

        Date d = new Date();
        this.title = title;
        this.status = status;
        this.description = description;
        this.tip = tip;
        this.creationTime = d.getTime();
        this.deliverTime = deliverTime;
        this.deliverLocation = deliverLocation;
        this.shopLocation = shopLocation;
        this.user = user;

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
         this.goodybagID = Long.parseLong(c);
     }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }

    public long getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(long deliverTime) {
        this.deliverTime = deliverTime;
    }

    public GeoLocation getDeliverLocation() {
        return deliverLocation;
    }

    public void setDeliverLocation(GeoLocation deliverLocation) {
        this.deliverLocation = deliverLocation;
    }

    public long getGoodybagID() {
        return goodybagID;
    }

    public void setGoodybagID(long goodybagID) {
        this.goodybagID = goodybagID;
    }

    public GeoLocation getShopLocation() {
        return shopLocation;
    }

    public void setShopLocation(GeoLocation shopLocation) {
        this.shopLocation = shopLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<User> getMatchedUsers() {
        return matchedUsers;
    }
}
