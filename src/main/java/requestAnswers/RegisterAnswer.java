package requestAnswers;

/**
 * Created by julianschweppe on 19.07.16.
 */
public class RegisterAnswer {

    private final boolean success;
    private final String reason;
    private final String accessToken;
    private final String refreshToken;
    private final long accessDuration;
    private final long refreshDuration;

    public RegisterAnswer(boolean success, String reason){
        this.success = success;
        this.reason = reason;
        this.refreshToken = "";
        this.accessToken = "";
        this.accessDuration = 0;
        this.refreshDuration = 0;
    }

    public RegisterAnswer(boolean success, String accessToken, Long accessDuration, String refreshToken, Long refreshDuration){
        this.success = success;
        this.reason = "";
        this.accessToken = accessToken;
        this.accessDuration = accessDuration;
        this.refreshToken = refreshToken;
        this.refreshDuration = refreshDuration;
    }

    public RegisterAnswer(boolean success){
        this.success = success;
        this.reason = "";
        this.accessToken = "";
        this.accessDuration = 0;
        this.refreshToken = "";
        this.refreshDuration = 0;
    }

    public long getAccessDuration() {
        return accessDuration;
    }

    public long getRefreshDuration() {
        return refreshDuration;
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
