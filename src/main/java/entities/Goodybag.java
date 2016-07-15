package entities;

/**
 * Created by Felix on 14.07.2016.
 */

public class Goodybag extends BaseModel{

    private GeoLocation geolocation;
    private String title;
    private int timestamp;
    private int radiusInMeters;
    private double tip;
    private int creationTime;
    private String description;
    private double longitude;
    private double latitude;
    private int deliveryTime;
    private String[] elements;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;
    private int creatorImage;

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    private String creatorName;

    public int getCreationTime() {
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

    public Goodybag(String title, String status, int creatorImage, String creatorName, int creationTime, int deliveryTime , String description, double tip, double longitude, double latitude, String[] elements ){
        this.title = title;
        this.creatorName=creatorName;
        this.creatorImage=creatorImage;
        this.status=status;
        this.creationTime=creationTime;
        this.description=description;
        this.tip=tip;
        this.longitude=longitude;
        this.latitude=latitude;
        this.deliveryTime=deliveryTime;
        this.elements=elements;
    }


    public GeoLocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(GeoLocation geolocation) {
        this.geolocation = geolocation;
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

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getRadiusInMeters() {
        return radiusInMeters;
    }

    public void setRadiusInMeters(int radiusInMeters) {
        this.radiusInMeters = radiusInMeters;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }
}
