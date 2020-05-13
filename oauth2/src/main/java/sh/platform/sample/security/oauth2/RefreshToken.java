package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;
import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.util.Objects;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class RefreshToken {

    @Id
    @JsonbProperty
    private String id;
    @JsonbProperty
    private String accessToken;
    @JsonbProperty
    private String user;

    @Deprecated
    public RefreshToken() {
    }

    RefreshToken(UserToken userToken, String accessToken, String user) {
        this.id = userToken.generateToken().get();
        this.accessToken = accessToken;
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

    void update(AccessToken refreshToken, UserToken userToken, KeyValueTemplate template) {
        template.delete(this.id);
        template.delete(this.accessToken);
        userToken.remove(Token.of(this.id));
        this.accessToken = refreshToken.getId();
        this.id = userToken.generateToken().get();
        template.put(this);
        template.put(userToken);
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
