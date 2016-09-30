package entities;

import java.util.*;

/**
 * Created by Felix Hambrecht on 11.07.2016.
 */
public class Route extends BaseModel{

    private GeoLocation connected_with;
    private GeoLocation toLocation;
    private User user;
    private String routeID;



    public Route(User user){

        this.user = user;

        Random r = new Random();
        char[] c = new char[255];
        for (int i = 0; i< c.length; i++){
            int rv = r.nextInt(75)+'0';
            if((rv >=58 && rv <=64)|| (rv >=91 && rv <=96) ) {
                i--;
                continue;
            }
            c[i] =(char)rv;
        }
        this.routeID = String.copyValueOf(c);
    }

    public Route(){
    }


    public void getLocationsInRoute(){
        //TODO
        //Iterate here the List of 'locations'
    }

    public User getUser() {
        return user;
    }

    public String getRouteID() {
        return routeID;
    }

    public GeoLocation getConnected_with() {
        return connected_with;
    }

    public void setConnected_with(GeoLocation connected_with) {
        this.connected_with = connected_with;
    }


}
