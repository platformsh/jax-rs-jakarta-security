package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Entity
public class UserToken {

    @Id
    private String username;

    @Column
    private Set<RefreshToken> tokens;

    UserToken(String username) {
        this.username = username;
    }

    @Deprecated
    UserToken() {
    }

    public String getUsername() {
        return username;
    }

    public Set<RefreshToken> getTokens() {
        if (tokens == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(tokens);
    }

    void add(RefreshToken refreshToken) {
        initiateTokens();
        this.tokens.add(refreshToken);
    }

    RefreshToken update(AccessToken accessToken, String refreshTokenText, UserTokenRepository repository) {
        initiateTokens();
        this.tokens.removeIf(r -> refreshTokenText.equals(r.getToken()));
        RefreshToken refreshToken = new RefreshToken(Token.generate(), accessToken);
        this.tokens.add(refreshToken);
        repository.save(this);
        return refreshToken;
    }

    void remove(String token) {
        initiateTokens();
        this.tokens.removeIf(r -> token.equals(r.getToken()));
    }

    Optional<AccessToken> findAccessToken(String accessToken) {
        initiateTokens();
        return this.tokens.stream().map(RefreshToken::getAccessToken)
                .filter(a -> a.getToken().equals(accessToken))
                .filter(AccessToken::isValid)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserToken userToken = (UserToken) o;
        return Objects.equals(username, userToken.username);
    }

    private void initiateTokens() {
        if (this.tokens == null) {
            this.tokens = new HashSet<>();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "username='" + username + '\'' +
                ", tokens=" + tokens +
                '}';
    }

}
