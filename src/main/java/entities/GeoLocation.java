package entities;

import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Felix Hambrecht on 11.07.2016.
 */
public class GeoLocation extends BaseModel {

    @Relationship(type = "CONNECTED_WITH", direction = Relationship.UNDIRECTED)
    private Set<GeoLocation> connectedSpots = new HashSet<>();
    private double longitude;
    private double latitude;
    private String title;
    private String address;
    private String GeoLocationID;
    private long userID;


    public GeoLocation(double longitude, double latitude, String address, String title) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.title = title;
        this.GeoLocationID = Double.toString(latitude)+Double.toString(longitude);
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

    public String getGeoLocationID() {
        return GeoLocationID;
    }
    public void setGeoLocationID(String GeoLocationID){
        this.GeoLocationID = GeoLocationID;
    }

    public Set<GeoLocation> getConnectedSpots() {
        return connectedSpots;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }
}
