package sh.platform.sample.security.oauth2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    public void shouldShouldCreateToken(){
        final Token token = Token.generate();
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.get());
        Assertions.assertEquals(Token.SIZE, token.get().length());
    }

}