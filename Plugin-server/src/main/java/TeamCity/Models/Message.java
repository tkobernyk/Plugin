package TeamCity.Models;

import java.util.Date;

public class Message {
    private String message;
    private String time;
    public Message(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return time.toString() + ": " + message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
