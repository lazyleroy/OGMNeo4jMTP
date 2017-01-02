package entities;

/**
 * Created by Felix Hambrecht on 15.08.2016.
 * Package: entities
 * Project: OGMNeo4jMTP
 */
public class Waypoint extends BaseModel {

    private User user;
    private String waypointID;

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

    private Spot spot;

    public Waypoint(){
    }

    public Waypoint(Spot spot, String waypointID){
        this.waypointID = waypointID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getWaypointID() {
        return waypointID;
    }
}
