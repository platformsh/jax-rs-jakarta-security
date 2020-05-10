package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import sh.platform.sample.security.User;
import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.util.Objects;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class AccessToken {

    @Id
    private String id;
    @JsonbProperty
    private String user;

    @Deprecated
    public  AccessToken() {
    }

    AccessToken(Oauth2Response response, String user) {
        this.id = response.getAccessToken();
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
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
