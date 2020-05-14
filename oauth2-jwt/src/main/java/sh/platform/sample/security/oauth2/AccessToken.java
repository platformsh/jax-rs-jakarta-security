package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.util.Objects;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class AccessToken {

    static final String PREFIX = "access_token:";

    @Id
    private String id;
    @JsonbProperty
    private String user;
    @JsonbProperty
    private String token;
    @JsonbProperty
    private String jwtSecret;


    @Deprecated
    public AccessToken() {
    }

    AccessToken(String jwt, Token token, String user) {
        this.token = jwt;
        this.id = PREFIX + jwt;
        this.jwtSecret = token.get();
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public Token getJwtSecretAsToken() {
        return Token.of(jwtSecret);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccessToken that = (AccessToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
