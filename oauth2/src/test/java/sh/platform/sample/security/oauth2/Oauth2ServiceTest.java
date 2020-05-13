package sh.platform.sample.security.oauth2;

import jakarta.nosql.mapping.keyvalue.KeyValueTemplate;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static sh.platform.sample.security.oauth2.Oauth2Service.EXPIRES;

@MockitoSettings(strictness = Strictness.WARN)
class Oauth2ServiceTest {


    @Mock
    private SecurityService securityService;

    @Mock
    private KeyValueTemplate template;

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
    public void shouldGenerateToken() {

        User user = User.builder().withName("otavio").withPassword("123")
                .withRoles(EnumSet.of(Role.ADMIN))
                .withPasswordHash(passwordHash)
                .build();

        when(securityService.findBy("otavio", "123"))
                .thenReturn(user);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        ArgumentCaptor<Iterable> iterableCaptor = ArgumentCaptor.forClass(Iterable.class);
        Oauth2Request request = new Oauth2Request();
        request.setGrandType(GrantType.PASSWORD);
        request.setPassword("123");
        request.setUsername("otavio");

        final Oauth2Response token = service.token(request);

        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getRefreshToken());
        assertEquals(Oauth2Service.EXPIRE_IN, token.getExpiresIn());

        Mockito.verify(template).put(captor.capture(), Mockito.eq(EXPIRES));
        Mockito.verify(template).put(iterableCaptor.capture());

        final Iterable value = iterableCaptor.getValue();
        final UserToken userToken = (UserToken) StreamSupport.stream(value.spliterator(), false)
                .filter(UserToken.class::isInstance)
                .map(UserToken.class::cast).findFirst().get();

        final AccessToken accessToken = (AccessToken) StreamSupport.stream(value.spliterator(), false)
                .filter(AccessToken.class::isInstance)
                .map(AccessToken.class::cast).findFirst().get();

        final RefreshToken refreshToken = captor.getValue();
        assertEquals(user.getName(), userToken.getUsername());
        assertEquals(userToken.getUsername(), refreshToken.getUser());
        assertEquals(accessToken.getId(), refreshToken.getAccessToken());
        assertThat(userToken.getTokens(), Matchers.containsInAnyOrder(Token.of(accessToken.getId())));
    }

    @Test
    public void shouldRefreshToken() {
        ArgumentCaptor<RefreshToken> refreshCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        ArgumentCaptor<AccessToken> accessCaptor = ArgumentCaptor.forClass(AccessToken.class);
        Oauth2Request request = new Oauth2Request();
        request.setGrandType(GrantType.REFRESH_TOKEN);
        request.setRefreshToken("refresh");

        UserToken userToken = new UserToken();
        final Token token1 = Token.generate();
        Mockito.when(template.get("refresh", RefreshToken.class))
                .thenReturn(Optional.of(new RefreshToken(userToken, token1.get(), "user")));

        service.refreshToken(request);
        Mockito.verify(template).put(accessCaptor.capture(), Mockito.eq(EXPIRES));
        Mockito.verify(template).put(refreshCaptor.capture());
        Mockito.verify(template, times(2)).delete(Mockito.anyString());

        final RefreshToken refreshToken = refreshCaptor.getValue();
        final AccessToken accessToken = accessCaptor.getValue();

        assertEquals(accessToken.getUser(), refreshToken.getUser());
        assertEquals(accessToken.getId(), refreshToken.getAccessToken());
    }

}