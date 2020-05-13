package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;
import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.util.List;
import java.util.Objects;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class RefreshToken {

    static final String PREFIX = "refresh_token:";

    @Id
    @JsonbProperty
    private String id;

    @JsonbProperty
    private String token;

    @JsonbProperty
    private String accessToken;
    @JsonbProperty
    private String user;

    @Deprecated
    public RefreshToken() {
    }

    RefreshToken(UserToken userToken, Token accessToken, String user) {
        this.token = userToken.generateToken().get();
        this.id = PREFIX + this.token;
        this.accessToken = accessToken.get();
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public String getId() {
        return id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getToken() {
        return token;
    }

    void update(AccessToken refreshToken, UserToken userToken, KeyValueTemplate template) {
        template.delete(PREFIX + this.id);
        template.delete(RefreshToken.PREFIX + this.accessToken);
        userToken.remove(Token.of(this.id));
        this.accessToken = refreshToken.getToken();
        this.token = userToken.generateToken().get();
        this.id = PREFIX + this.token;
        template.put(List.of(this, userToken));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
