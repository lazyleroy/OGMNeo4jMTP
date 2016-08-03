package entities;

import java.util.Date;
import java.util.Random;

/**
 * Created by Felix on 15.07.2016.
 */
public class Cookie extends  BaseModel{

    private long expiresAt;
    private String refreshToken;
    private User user;

    public Cookie(User user){
        Date date = new Date();
        this.expiresAt = date.getTime()+ 15768000000L;
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
        this.refreshToken = String.copyValueOf(c);
    }

    public Cookie(){}

    public long getExpiresAt(){return this.expiresAt;}
    public java.lang.String getRefreshToken() {return refreshToken;}
    public User getUser(){return this.user;}

}
