package entities;

/**
 * Created by julianschweppe on 19.07.16.
 */
public class RegisterAnswer {

    private final boolean success;
    private final String reason;
    private final String accessToken;
    private final String refreshToken;


    public RegisterAnswer(boolean success, String reason){
        this.success = success;
        this.reason = reason;
        this.refreshToken = "";
        this.accessToken = "";
    }

    public RegisterAnswer(boolean success, String accessToken, String refreshToken){
        this.success = success;
        this.reason = "";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getReason(){
        return this.reason;
    }

    public boolean getSucsess(){
        return this.success;
    }

    public String getAccessToken() {return this.accessToken; }

    public String getRefreshToken(){return this.refreshToken; }

}
