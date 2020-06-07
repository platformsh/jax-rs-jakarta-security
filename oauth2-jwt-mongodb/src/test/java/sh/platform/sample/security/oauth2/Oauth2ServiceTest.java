package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.document.DocumentTemplate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sh.platform.sample.security.Role;
import sh.platform.sample.security.SecurityService;
import sh.platform.sample.security.User;

import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.validation.Validator;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static sh.platform.sample.security.oauth2.Oauth2Service.EXPIRES;

@MockitoSettings(strictness = Strictness.WARN)
class Oauth2ServiceTest {


    @Mock
    private SecurityService securityService;

    @Mock
    private UserTokenRepository repository;

    @Mock
    private Pbkdf2PasswordHash passwordHash;

    @Mock
    private Validator validator;

    @InjectMocks
    private Oauth2Service service;

    public void setUp() {
        when(passwordHash.generate(Mockito.any(char[].class)))
                .thenReturn("hashPassword");
    }

    @Test
    public void shouldGenerateTokenFromNewUser() {
        ArgumentCaptor<UserToken> captor = ArgumentCaptor.forClass(UserToken.class);
        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();

        when(securityService.findBy("otavio", "123"))
                .thenReturn(user);

        Oauth2Request request = new Oauth2Request();
        request.setGrandType(GrantType.PASSWORD);
        request.setPassword("123");
        request.setUsername("otavio");

        final Oauth2Response token = service.token(request);

        Mockito.verify(repository).save(captor.capture());
        final UserToken userToken = captor.getValue();
        final RefreshToken refreshToken = userToken.getTokens().stream().findFirst().get();
        final AccessToken accessToken = refreshToken.getAccessToken();
        assertEquals(1, userToken.getTokens().size());
        assertEquals(user.getName(), userToken.getUsername());
        assertEquals(token.getAccessToken(), accessToken.getToken());
        assertEquals(token.getRefreshToken(), refreshToken.getToken());
    }

    @Test
    public void shouldRefreshToken() {
        ArgumentCaptor<UserToken> captor = ArgumentCaptor.forClass(UserToken.class);
        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();


        AccessToken accessToken = new AccessToken("access", "jwt", EXPIRES);
        RefreshToken refreshToken = new RefreshToken(Token.of("refresh"), accessToken);
        UserToken userToken = new UserToken("otavio");
        userToken.add(refreshToken);

        when(securityService.findBy("otavio"))
                .thenReturn(user);
        Mockito.when(repository.findByRefreshToken("refresh")).thenReturn(Optional.of(userToken));

        Oauth2Request request = new Oauth2Request();
        request.setGrandType(GrantType.REFRESH_TOKEN);
        request.setRefreshToken("refresh");

        final Oauth2Response response = service.refreshToken(request);

        Mockito.verify(repository).save(captor.capture());
        final UserToken value = captor.getValue();
        assertEquals(1, value.getTokens().size());
        final RefreshToken refreshToken1 = value.getTokens().stream().findFirst().get();
        final AccessToken accessToken1 = refreshToken1.getAccessToken();
        assertEquals(user.getName(), userToken.getUsername());
        assertEquals(response.getAccessToken(), accessToken1.getToken());
        assertEquals(response.getRefreshToken(), refreshToken1.getToken());

    }

}