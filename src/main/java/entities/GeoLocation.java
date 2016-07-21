package entities;

/**
 * Created by Felix Hambrecht on 11.07.2016.
 */
public class GeoLocation extends BaseModel {

    private double longitude;

    private double latitude;


    public GeoLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    GeoLocation(){
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
