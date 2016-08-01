package entities;

/**
 * Created by Felix on 01.08.2016.
 */
public class FirebaseToken extends BaseModel {



    private String token;
    private User user;

    public FirebaseToken(){
    }

    public FirebaseToken(String token, User user){
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
