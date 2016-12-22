package Interfaces;

import entities.*;
import org.neo4j.ogm.model.Result;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by simon on 10.11.2016.
 */
public interface DBController {
    /**
     * Automatically adds a spot to the db
     *
     * @param spot :Spot to add to the db
     */
    void addSpot(Spot spot);

    /**
     * Updates the spot in the db
     *
     * @param spot :Spot to remove from the db
     */
    void updateSpot(Spot spot);

    /**
     * Returns a particular spot with a given ID
     *
     * @param spotID :ID of the Spot
     * @return Spot :searched Spot
     */
    Spot getSpot(String spotID);

    /**
     *
     * @param latitude : latitude of GPS location
     * @param longitude: longitude of GPS location
     * @return Spot: list of searched Spots
     */
    ArrayList<Spot> getSpots(float latitude, float longitude);

    void addGPSPoints(ArrayList<GPS_plus> gpspoints, String username, ArrayList<String> spots);

    void addNeighbour(String neigbourID, String updatedSpotID, boolean intersectionCheck, boolean updatedIntersectionCheck);

    void setIntersections(ArrayList<String> spots);

    Result sendQuery(String query);
}
