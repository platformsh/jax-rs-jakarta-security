package sh.platform.sample.security.oauth2;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;


public final class Token implements Supplier<String> {

    private static final int LEFT_LIMIT = 48;
    private static final int RIGHT_LIMIT = 122;
    static final int SIZE = 32;

    @JsonbProperty("token")
    private final String token;

    @JsonbCreator
    public Token(@JsonbProperty("token") String token) {
        this.token = token;
    }

    @Override
    public String get() {
        return token;
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Token token1 = (Token) o;
        return Objects.equals(token, token1.token);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(token);
    }

    public static Token of(String value) {
        Objects.requireNonNull(value, "value is required");
        return new Token(value);
    }

    public static Token generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String token = random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(SIZE)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return new Token(token);
    }
}
