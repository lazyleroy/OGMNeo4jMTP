package requestAnswers;

/**
 * Created by Julian Schweppe on 19.07.16.
 */
public class SimpleAnswer {

    private final boolean success;
    private final String reason;

    public SimpleAnswer(boolean success, String reason){
        this.success = success;
        this.reason = reason;
    }

    public SimpleAnswer(boolean success){
        this.success = success;
        this.reason = "";
    }

    public String getReason() {
        return this.reason;
    }

    public boolean getSuccess() {
        return this.success;
    }

}
