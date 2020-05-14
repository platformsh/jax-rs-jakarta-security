package sh.platform.sample.security.oauth2;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sh.platform.sample.security.Role;
import sh.platform.sample.security.User;

import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.time.Duration;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@MockitoSettings(strictness = Strictness.WARN)
class UserJWTTest {


    @Mock
    private Pbkdf2PasswordHash passwordHash;

    @Test
    public void shouldCreateToken() {
        Duration duration = Duration.ofMinutes(30L);
        Token token = Token.generate();
        User user = User.builder()
                .withPassword("password")
                .withName("user")
                .withRoles(EnumSet.of(Role.ADMIN, Role.USER))
                .withPasswordHash(passwordHash).build();

        final String jwt = UserJWT.createToken(user, token, duration);
        Assertions.assertNotNull(jwt);
    }


    @Test
    public void shouldCreateFromToken() {
        Duration duration = Duration.ofMinutes(30L);
        Token token = Token.generate();
        User user = User.builder()
                .withPassword("password")
                .withName("user")
                .withRoles(EnumSet.of(Role.ADMIN, Role.USER))
                .withPasswordHash(passwordHash).build();

        final String jwt = UserJWT.createToken(user, token, duration);
        final UserJWT parse = UserJWT.parse(jwt, token).get();

        Assertions.assertEquals(user.getName(), parse.getUser());
        MatcherAssert.assertThat(user.getRoles(), Matchers.containsInAnyOrder("ADMIN", "USER"));
    }


    @Test
    public void shouldReturnExpireError() throws InterruptedException {
        Duration duration = Duration.ofSeconds(1L);
        Token token = Token.generate();
        User user = User.builder()
                .withPassword("password")
                .withName("user")
                .withRoles(EnumSet.of(Role.ADMIN, Role.USER))
                .withPasswordHash(passwordHash).build();

        final String jwt = UserJWT.createToken(user, token, duration);
        TimeUnit.SECONDS.sleep(3L);
        final Optional<UserJWT> parse = UserJWT.parse(jwt, token);
      Assertions.assertFalse(parse.isPresent());
    }


}