package EntityWrappers;

import entities.GeoLocation;

/**
 * Created by Felix on 21.07.2016.
 */
public class GoodybagWrapper {

    private GeoLocation deliverLocation;
    private GeoLocation shopLocation;
    private String title;
    private String status;
    private String description;
    private long deliverTime;
    private double tip;
    private int checkOne;
    private int checkTwo;


    public GeoLocation getDeliverLocation() {
        return deliverLocation;
    }

    public GeoLocation getShopLocation() {
        return shopLocation;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public long getDeliverTime() {
        return deliverTime;
    }

    public double getTip() {
        return tip;
    }

    public int getCheckOne() {
        return checkOne;
    }

    public int getCheckTwo() {
        return checkTwo;
    }
}
