package entities;

import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Felix on 11.07.2016.
 */
public class Route extends BaseModel{

    @Relationship(type = "CONTAINS", direction = Relationship.OUTGOING)
    private ArrayList<GeoLocation> spots;
    private GeoLocation fromLocation;
    private GeoLocation toLocation;
    private User user;
    private String routeID;

    public Route(ArrayList<GeoLocation> spots, User user){
        this.spots = spots;
        this.user = user;

        Random r = new Random();
        char[] c = new char[255];
        for (int i = 0; i< c.length; i++){
            int rv = r.nextInt(75)+'0';
            if((rv >=58 && rv <=64)|| (rv >=91 && rv <=96) ) {
                i--;
                continue;
            }
            c[i] =(char)rv;
        }
        this.routeID = String.copyValueOf(c);
    }

    public Route(){
    }

    public Route(ArrayList<GeoLocation> spots){this.spots=spots;
    }

    public void getDistance(GeoLocation fromLocation, GeoLocation toLocation){
        //TODO
    }

    public void getLocationsInRoute(){
        //TODO
        //Iterate here the List of 'locations'
    }
    public List<GeoLocation> getSpots(){
        return spots;
    }


    public User getUser() {
        return user;
    }

    public String getRouteID() {
        return routeID;
    }
}
