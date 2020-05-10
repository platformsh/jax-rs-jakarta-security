package sh.platform.sample.security;

import java.util.Collections;
import java.util.List;

public class UserAlreadyExistException extends RuntimeException {

    private String message;

    public UserAlreadyExistException(String message) {
        super(message);
        this.message = message;
    }

    public List<String> getMessages() {
        return Collections.singletonList(message);
    }
}
