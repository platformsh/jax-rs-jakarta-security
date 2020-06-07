package sh.platform.sample.security.oauth2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class AccessTokenTest {

    @Test
    public void shouldBeValid() {
        AccessToken accessToken = new AccessToken("token", "secret", Duration.ofSeconds(2L));
        Assertions.assertTrue(accessToken.isValid());
    }

    @Test
    public void shouldNotBeValid() {
        AccessToken accessToken = new AccessToken("token", "secret", Duration.ZERO);
        Assertions.assertFalse(accessToken.isValid());
    }
}