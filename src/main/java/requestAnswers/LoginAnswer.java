package requestAnswers;

/**
 * Created by Felix on 08.08.2016.
 * Package: requestAnswers
 * Project: OGMNeo4jMTP
 */
public class LoginAnswer {

    private final boolean success;
    private final String reason;
    private final String accessToken;
    private final String refreshToken;
    private final long accessDuration;
    private final long refreshDuration;
    private final String picturePath;

    public LoginAnswer(boolean success, String reason){
        this.success = success;
        this.reason = reason;
        this.refreshToken = "";
        this.accessToken = "";
        this.accessDuration = 0;
        this.refreshDuration = 0;
        this.picturePath = "";
    }

    public LoginAnswer(boolean success, String accessToken, Long accessDuration, String refreshToken, Long refreshDuration, String picturePath){
        this.success = success;
        this.reason = "";
        this.accessToken = accessToken;
        this.accessDuration = accessDuration;
        this.refreshToken = refreshToken;
        this.refreshDuration = refreshDuration;
        this.picturePath = picturePath;
    }

}
