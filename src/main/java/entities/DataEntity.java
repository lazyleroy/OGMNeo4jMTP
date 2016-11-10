package entities;

/**
 * Created by Felix on 20.10.2016.
 * Package: entities
 * Project: OGMNeo4jMTP
 */
public class DataEntity extends BaseModel {

    public double longitude;
    public double latitude;
    public double acceleration;
    public double volume;
    public long time;
    public long track;
    public long nodeNumber;



    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public long getTrack() {
        return track;
    }

    public void setTrack(long track) {
        this.track = track;
    }

    public long getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(long nodeNumber) {
        this.nodeNumber = nodeNumber;
    }



}
