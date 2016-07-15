package entities;

import java.util.Date;

/**
 * Created by Felix on 14.07.2016.
 */
public class UserSession extends BaseModel {

    private long accessToken;
    private User user;

    public UserSession(User user){
        Date date = new Date();
        this.accessToken = date.getTime();
        this.user = user;
    }
    public UserSession(){}

    public long getAccessToken(){return accessToken;}
    public User getUser(){return this.user;}
}
