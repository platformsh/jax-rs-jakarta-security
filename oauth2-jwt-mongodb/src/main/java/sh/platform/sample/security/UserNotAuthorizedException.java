package sh.platform.sample.security;

import java.util.Collections;
import java.util.List;

public class UserNotAuthorizedException extends RuntimeException {

    private String message;

    public UserNotAuthorizedException(String message) {
        super(message);
        this.message = message;
    }

    public UserNotAuthorizedException() {
        this.message = "User not authorized";
    }

    public List<String> getMessages() {
        return Collections.singletonList(message);
    }
}
