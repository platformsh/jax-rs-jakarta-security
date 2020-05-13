package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;
import sh.platform.sample.security.infra.FieldPropertyVisibilityStrategy;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbVisibility;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@JsonbVisibility(FieldPropertyVisibilityStrategy.class)
public class UserToken {

    @Id
    @JsonbProperty
    private String username;

    @Column
    @JsonbProperty
    private Set<Token> tokens;


    public UserToken() {
    }

    UserToken(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Set<Token> getTokens() {
        if (tokens == null) {
            return Collections.emptySet();
        }
        return tokens.stream().collect(Collectors.toUnmodifiableSet());
    }

    void remove(Token token) {
        if (tokens != null) {
            this.tokens.remove(token);
        }
    }

    public Token generateToken() {
        Token token = Token.generate();
        if (tokens == null) {
            this.tokens = new HashSet<>();
        }
        this.tokens.add(token);
        return token;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
