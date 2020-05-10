package sh.platform.sample.security.oauth2;

import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.util.Objects;

@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class Oauth2Response {

    @JsonbProperty("access_token")
    private String accessToken;

    @JsonbProperty("username")
    private int expiresIn;

    @JsonbProperty("refresh_token")
    private String refreshToken;


    public String getAccessToken() {
        return accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public static Oauth2Response of(Token token, int expiresIn) {
        Objects.requireNonNull(token, "token is required");
        Oauth2Response response = new Oauth2Response();
        response.accessToken = token.get();
        response.refreshToken = Token.generate().get();
        response.expiresIn = expiresIn;
        return response;
    }


}
