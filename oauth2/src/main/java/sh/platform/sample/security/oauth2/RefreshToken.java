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

    RefreshToken(String accessToken, String user) {
        this.id = Token.generate().get();
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

    void update(AccessToken refreshToken, KeyValueTemplate template) {
        template.delete(this.id);
        template.delete(this.accessToken);
        this.accessToken = refreshToken.getId();
        this.id = Token.generate().get();
        template.put(this);
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
