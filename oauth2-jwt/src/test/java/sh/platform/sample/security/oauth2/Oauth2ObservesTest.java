package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sh.platform.sample.security.RemoveToken;
import sh.platform.sample.security.RemoveUser;
import sh.platform.sample.security.Role;
import sh.platform.sample.security.User;

import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.EnumSet;
import java.util.Optional;

@MockitoSettings(strictness = Strictness.WARN)
class Oauth2ObservesTest {

    @Mock
    private KeyValueTemplate template;

    @InjectMocks
    private Oauth2Observes observes;

    @Mock
    private Pbkdf2PasswordHash passwordHash;

    @Test
    public void shouldRemoveUser() {

        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();

        UserToken userToken = new UserToken("otavio");
        final Token token = userToken.generateToken();

        final String jwt = UserJWT.createToken(user, token, Oauth2Service.EXPIRES);
        AccessToken accessToken = new AccessToken(jwt, token, user.getName());
        RefreshToken refreshToken = new RefreshToken(userToken, jwt, user.getName());

        final Oauth2Response oauth2Response = Oauth2Response.of(accessToken, refreshToken, 10);
        Mockito.when(template.get(RefreshToken.PREFIX + token.get(), RefreshToken.class)).thenReturn(Optional.of(refreshToken));
        Mockito.when(template.get(user.getName(), UserToken.class)).thenReturn(Optional.of(userToken));
        observes.observe(new RemoveUser(user));
        Mockito.verify(template).delete("otavio");
        Mockito.verify(template).delete(RefreshToken.PREFIX + oauth2Response.getRefreshToken());
        Mockito.verify(template).delete(AccessToken.PREFIX + oauth2Response.getAccessToken());
    }

    @Test
    public void shouldRemoveToken() {

        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();

        UserToken userToken = new UserToken();
        final Token token = userToken.generateToken();
        final String jwt = UserJWT.createToken(user, token, Oauth2Service.EXPIRES);
        AccessToken accessToken = new AccessToken(jwt, token, user.getName());
        RefreshToken refreshToken = new RefreshToken(userToken, jwt, user.getName());
        final Oauth2Response oauth2Response = Oauth2Response.of(accessToken, refreshToken, 10);
        RemoveToken removeToken = new RemoveToken(user, "token");
        Mockito.when(template.get(RefreshToken.PREFIX + "token", RefreshToken.class)).thenReturn(Optional.of(refreshToken));

        observes.observe(removeToken);
        Mockito.verify(template).delete(RefreshToken.PREFIX + oauth2Response.getRefreshToken());
        Mockito.verify(template).delete(AccessToken.PREFIX + oauth2Response.getAccessToken());
    }
}