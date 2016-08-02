package entities;

import java.util.Random;

/**
 * Created by Felix on 14.07.2016.
 */

public class Goodybag extends BaseModel{


    private String creatorName;
    private int creatorImage;
    private String title;
    private String status;
    private String description;
    private double tip;
    private long creationTime;
    private long deliverTime;
    private GeoLocation deliverLocation;
    private GeoLocation shopLocation;
    private String goodyBagID;
    private User user;

    public Goodybag(){
    }

    public Goodybag(String creatorName, int creatorImage, String title, String status, String description,
                    double tip, long creationTime, long deliverTime, GeoLocation deliverLocation,
                    GeoLocation shopLocation, User user){
        this.creatorName = creatorName;
        this.creatorImage = creatorImage;
        this.title = title;
        this.status = status;
        this.description = description;
        this.tip = tip;
        this.creationTime = creationTime;
        this.deliverTime = deliverTime;
        this.deliverLocation = deliverLocation;
        this.shopLocation = shopLocation;
        this.user = user;
        changeID();

            }


     public void changeID(){
         Random r = new Random();
         char[] c = new char[25];
         for (int i = 1; i< c.length; i++){
             int rv = r.nextInt(10)+'0';
             if(rv >=58 ) {
                 i--;
                 continue;
             }
             if(i%5==0){
                 c[i]= '-';
                 continue;
             }
             c[i] =(char)rv;
         }
         this.goodyBagID = String.copyValueOf(c);
     }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(int creationTime) {
        this.creationTime = creationTime;
    }

    public int getCreatorImage() {
        return creatorImage;
    }

    public void setCreatorImage(int creatorImage) {
        this.creatorImage = creatorImage;
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

    public String getGoodyBagID() {
        return goodyBagID;
    }

    public void setGoodyBagID(String goodyBagID) {
        this.goodyBagID = goodyBagID;
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
}
