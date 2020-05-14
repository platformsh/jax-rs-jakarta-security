package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import sh.platform.sample.security.RemoveToken;
import sh.platform.sample.security.RemoveUser;
import sh.platform.sample.security.User;
import sh.platform.sample.security.UserForbiddenException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
class Oauth2Observes {


    @Inject
    @ConfigProperty(name = "keyvalue")
    private KeyValueTemplate template;

    public void observe(@Observes RemoveUser removeUser) {

        final User user = removeUser.getUser();
        final Set<Token> tokens = template.get(user.getName(), UserToken.class)
                .map(UserToken::getTokens)
                .orElse(Collections.emptySet());

        tokens.stream().map(t -> template.get(RefreshToken.PREFIX + t.get(), RefreshToken.class))
                .forEach(o -> o.ifPresent(this::deleteTokens));

        template.delete(user.getName());
    }

    public void observe(@Observes RemoveToken removeToken) {

        final Optional<RefreshToken> refreshTokenOptional = template.get(RefreshToken.PREFIX + removeToken.getToken(),
                RefreshToken.class);
        if (refreshTokenOptional.isPresent()) {
            final RefreshToken refreshToken = refreshTokenOptional.get();
            final User user = removeToken.getUser();
            if (userCanRemoveToken(refreshToken, user)) {
                deleteTokens(refreshToken);
                return;
            }
            throw new UserForbiddenException();
        }

    }

    private void deleteTokens(RefreshToken token) {
        template.delete(AccessToken.PREFIX + token.getAccessToken());
        template.delete(token.getId());
    }

    private boolean userCanRemoveToken(RefreshToken accessToken, User user) {
        return accessToken.getUser().equals(user.getName()) || user.isAdmin();
    }

}
