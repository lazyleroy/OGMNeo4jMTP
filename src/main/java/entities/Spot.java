package entities;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Felix Hambrecht on 12.08.2016.
 * Package: entities
 * Project: OGMNeo4jMTP
 */
@NodeEntity
public class Spot extends BaseModel {


    @Relationship(type = "CONNECTED_WITH", direction = Relationship.UNDIRECTED)
    private Set<Spot> connectedSpots = new HashSet<>();

    private long spotID;

    public Spot(long spotID, HashSet<Spot>connectedSpots){
        this.spotID = spotID;
        this.connectedSpots = connectedSpots;
    }

    public Spot(){
    }

    public long getSpotID() {
        return spotID;
    }

    public void setSpotID(long spotID) {
        this.spotID = spotID;
    }

    public Set<Spot> getConnectedSpots() {
        return connectedSpots;
    }

    public void setConnectedSpots(Set<Spot> connectedSpots) {
        this.connectedSpots = connectedSpots;
    }
}
