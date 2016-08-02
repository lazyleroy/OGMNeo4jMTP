package EntityWrappers;

import entities.GeoLocation;

import java.util.ArrayList;

/**
 * Created by Felix on 22.07.2016.
 */
public class RouteWrapper {

    private ArrayList<GeoLocation> route;
    private String accessToken;

    public ArrayList<GeoLocation> getRoute() {
        return route;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
