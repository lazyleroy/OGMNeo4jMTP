package entities;

import java.util.Date;
import java.util.Random;

/**
 * Created by Felix on 14.07.2016.
 */
public class UserSession extends BaseModel{

    private long expiresAt;
    private String accessToken;
    private User user;

    public UserSession(User user){

        Date date = new Date();
        this.expiresAt = date.getTime()+86400000L;
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
        this.accessToken = String.copyValueOf(c);
    }
    public UserSession(){}

    public long getExpiresAt(){return this.expiresAt;}
    public String getAccessToken(){return this.accessToken;}
    public User getUser(){return this.user;}
    public void setExpiresAt(long expiresAt){
        this.expiresAt = expiresAt;
    }
}
