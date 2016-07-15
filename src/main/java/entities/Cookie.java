package entities;

import java.util.Date;

/**
 * Created by Felix on 15.07.2016.
 */
public class Cookie extends  BaseModel{

    private long refreshToken;
    private User user;

    public Cookie(User user){
        Date date = new Date();
        this.refreshToken = date.getTime();
        this.user = user;
    }
    public Cookie(){}

    public long getRefreshToken(){return this.refreshToken;}
    public User getUser(){return this.user;}

}
