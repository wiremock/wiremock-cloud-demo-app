package mocklab.demo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMessage {

    private final String message;

    public ResponseMessage(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
