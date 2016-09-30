package entities;

/**
 * Created by Felix Hambrecht on 01.08.2016.
 */
public class FirebaseToken extends BaseModel {



    private String token;
    private Cookie cookie;

    public FirebaseToken(){
    }

    public FirebaseToken(String token, Cookie cookie){
        this.token = token;
        this.cookie = cookie;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(Cookie cookie) {
        this.cookie = cookie;
    }
}
