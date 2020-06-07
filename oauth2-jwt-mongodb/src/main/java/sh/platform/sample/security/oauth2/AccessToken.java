package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class AccessToken {

    @Column
    private String token;

    @Column
    private String jwtSecret;

    @Column
    private LocalDateTime expired;


    @Deprecated
    AccessToken() {
    }

    AccessToken(String token, String jwtSecret, Duration duration) {
        this.token = token;
        this.jwtSecret = jwtSecret;
        this.expired = LocalDateTime.now().plus(duration);
    }

    public String getToken() {
        return token;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public LocalDateTime getExpired() {
        return expired;
    }

    public boolean isValid() {
        final LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expired);
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "token='" + token + '\'' +
                ", jwtSecret='" + jwtSecret + '\'' +
                ", expired=" + expired +
                '}';
    }
}
