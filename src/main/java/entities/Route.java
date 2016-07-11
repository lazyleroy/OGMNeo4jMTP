package entities;

import java.util.List;

/**
 * Created by Felix on 11.07.2016.
 */
public class Route extends GeoLocation {
    private List<GeoLocation> locations;
    private GeoLocation fromLocation;
    private GeoLocation toLocation;

    public Route(List<GeoLocation> locations){
        this.locations=locations;
    }

    public void getDistance(GeoLocation fromLocation, GeoLocation toLocation){
        //TODO
    }
    public void getLocationsInRoute(){
        //TODO
        //Iterate here the List of 'locations'
    }
}
