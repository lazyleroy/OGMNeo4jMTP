package requestAnswers;

/**
 * Created by julianschweppe on 19.07.16.
 */
public class RegisterAnswer {

    private final boolean success;
    private final String reason;
    private final String accessToken;
    private final String refreshToken;
    private final long accessExpires;
    private final long refreshExpires;

    public RegisterAnswer(boolean success, String reason){
        this.success = success;
        this.reason = reason;
        this.refreshToken = "";
        this.accessToken = "";
        this.accessExpires = 0;
        this.refreshExpires = 0;
    }

    public RegisterAnswer(boolean success, String accessToken, Long accessExpires, String refreshToken, Long refreshExpires){
        this.success = success;
        this.reason = "";
        this.accessToken = accessToken;
        this.accessExpires = accessExpires;
        this.refreshToken = refreshToken;
        this.refreshExpires = refreshExpires;
    }

    public RegisterAnswer(boolean success){
        this.success = success;
        this.reason = "";
        this.accessToken = "";
        this.accessExpires = 0;
        this.refreshToken = "";
        this.refreshExpires = 0;
    }

    public long getAccessExpires() {
        return accessExpires;
    }

    public long getRefreshExpires() {
        return refreshExpires;
    }


    public String getReason() {
        return this.reason;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

}
