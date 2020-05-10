package sh.platform.sample.security;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super("User does not found with username: " + id);
    }
}
