package EntityWrappers;

import entities.GeoLocation;
import entities.Spot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Felix on 22.07.2016.
 */
public class RouteWrapper {

    private Map<GeoLocation, Spot>route;

    public Map<GeoLocation, Spot> getRoute() {
        return route;
    }
}
