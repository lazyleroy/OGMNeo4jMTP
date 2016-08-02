package entities;

/**
 * Created by Felix Hambrecht on 11.07.2016.
 */
public class GeoLocation extends BaseModel {

    private double longitude;
    private double latitude;
    private int GeoLocationID;


    public GeoLocation(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.GeoLocationID = 0;
    }
    public GeoLocation(){
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

    public int getGeoLocationID() {
        return GeoLocationID;
    }
    public void setGeoLocationID(int GeoLocationID){
        this.GeoLocationID = GeoLocationID;
    }

}
