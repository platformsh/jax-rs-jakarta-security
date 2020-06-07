package sh.platform.sample.security.oauth2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import java.time.Duration;
import java.util.EnumSet;
import java.util.Optional;

@MockitoSettings(strictness = Strictness.WARN)
class Oauth2ObservesTest {

    @Mock
    private UserTokenRepository repository;

    @InjectMocks
    private Oauth2Observes observes;

    @Mock
    private Pbkdf2PasswordHash passwordHash;

    @Captor
    private ArgumentCaptor<UserToken> captor;

    @Test
    public void shouldRemoveUser() {

        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();

        observes.observe(new RemoveUser(user));
        Mockito.verify(repository).deleteById("otavio");
    }

    @Test
    public void shouldRemoveToken() {

        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();

        UserToken userToken = new UserToken(user.getName());
        AccessToken accessToken = new AccessToken("token", "expire", Duration.ZERO);
        RefreshToken refreshToken = new RefreshToken(Token.of("refresh"), accessToken);
        userToken.add(refreshToken);

        Mockito.when(repository.findById("otavio")).thenReturn(Optional.of(userToken));

        observes.observe(new RemoveToken(user, "refresh"));
        Mockito.verify(repository).findById("otavio");

        Mockito.verify(repository).save(captor.capture());
        final UserToken value = captor.getValue();
        Assertions.assertTrue(value.getTokens().isEmpty());

    }
}