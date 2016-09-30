package entities;

import java.util.Date;
import java.util.Random;

/**
 * Created by Felix Hambrecht on 14.07.2016.
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
    private String goodybagID;
    private User user;
    private int checkOne;
    private int checkTwo;



    public Goodybag(){
    }

    public Goodybag(String title, String status, String description,
                    double tip, long deliverTime, GeoLocation deliverLocation,
                    GeoLocation shopLocation, User user, int checkOne, int checkTwo){

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
        this.checkOne = checkOne;
        this.checkTwo = checkTwo;

    }


    public void changeID(){
        Random r = new Random();
        char[] c = new char[5];
        for (int i = 0; i< c.length; i++){
            int rv = r.nextInt(75)+'0';
            if((rv >=58 && rv <=64)|| (rv >=91 && rv <=96) ) {
                i--;
                continue;
            }
            c[i] =(char)rv;
        }
        this.goodybagID = String.copyValueOf(c);
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

    public String getGoodybagID() {
        return goodybagID;
    }

    public void setGoodybagID(String goodybagID) {
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



    public int getCheckOne() {
        return checkOne;
    }
    public int getCheckTwo() {
        return checkTwo;
    }

}
