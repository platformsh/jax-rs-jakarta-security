package sh.platform.sample.security.oauth2;

import sh.platform.sample.security.RemoveToken;
import sh.platform.sample.security.RemoveUser;
import sh.platform.sample.security.User;
import sh.platform.sample.security.UserForbiddenException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
class Oauth2Observes {


    @Inject
    private UserTokenRepository repository;

    public void observe(@Observes RemoveUser removeUser) {
        final User user = removeUser.getUser();
        repository.deleteById(user.getName());
    }

    public void observe(@Observes RemoveToken removeToken) {
        final User user = removeToken.getUser();
        final String token = removeToken.getToken();
        UserToken userToken = repository.findById(user.getName())
                .orElseThrow(() -> new IllegalArgumentException("User was not found: " + user.getName()));
        userToken.remove(token);
        repository.save(userToken);
    }

}
