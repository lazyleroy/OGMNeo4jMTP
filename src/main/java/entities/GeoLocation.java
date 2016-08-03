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
    private String GeoLocationID;

    public GeoLocation(double longitude, double latitude, String id) {
        this.longitude = longitude;
        this.latitude = latitude;
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
}
