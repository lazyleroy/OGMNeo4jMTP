package entities;

import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 11.07.2016.
 */
public class Route extends BaseModel{

    @Relationship(type = "CONTAINS", direction = Relationship.OUTGOING)
    private List<GeoLocation> locations;
    private GeoLocation fromLocation;
    private GeoLocation toLocation;

    public Route(){
    }

    public Route(List<GeoLocation> locations){this.locations=locations;
    }

    public void getDistance(GeoLocation fromLocation, GeoLocation toLocation){
        //TODO
    }

    public void getLocationsInRoute(){
        //TODO
        //Iterate here the List of 'locations'
    }
    public List<GeoLocation> getLocations(){
        return locations;
    }




}
