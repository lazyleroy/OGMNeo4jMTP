package EntityWrappers;

import entities.GeoLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 21.07.2016.
 */
public class GoodybagWrapper {

    private GeoLocation deliverLocation;
    private GeoLocation shopLocation;
    private String creatorName;
    private String title;
    private String status;
    private String description;
    private String accessToken;
    private long creationTime;
    private long deliverTime;
    private double tip;
    private int creatorImage;


    public GeoLocation getDeliverLocation() {
        return deliverLocation;
    }

    public GeoLocation getShopLocation() {
        return shopLocation;
    }

    public String getCreatorName() {
        return creatorName;
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

    public String getAccessToken() {
        return accessToken;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getDeliverTime() {
        return deliverTime;
    }

    public double getTip() {
        return tip;
    }

    public int getCreatorImage() {
        return creatorImage;
    }
}
