package LocationProcessorServer.controller;

import LocationProcessorServer.datastructures.GPS_plus;
import entities.Spot;
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
     * Automatically removes a spot from the db
     *
     * @param spot :Spot to remove from the db
     */
    void removeSpot(Spot spot);

    /**
     * Returns a particular spot with a given ID and center-point
     *
     * @param spotID :ID of the Spot
     * @param point  :GPS location of the Spot
     * @return Spot :searched Spot
     */
    Spot getSpot(int spotID, GPS_plus point);

    /**
     * Returns a particular spot with a given ID and center-point
     *
     * @param spotID :ID of the Spot
     * @param latitude :latitude
     * @param longitude :longitude
     * @return Spot :searched Spot
     */
    Spot getSpot(long spotID, float latitude, float longitude);

    /**
     * Returns all spots stored in a specific radius
     *
     * @param arg1 :latitude-index
     * @param arg2 :longitude-index
     * @param radius :radius
     * @return ArrayList<Spot> :searched Spots
     */
    ArrayList<Spot> getSpots(int arg1, int arg2, double radius);
}
