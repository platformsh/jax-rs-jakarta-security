package sh.platform.sample.security;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class SecurityServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private Pbkdf2PasswordHash passwordHash;

    @Mock
    private SecurityContext context;

    @InjectMocks
    private SecurityService service;

    @BeforeEach
    public void setUp() {
        when(passwordHash.generate(Mockito.any(char[].class)))
                .thenReturn("hashPassword");
    }

    @Test
    public void shouldCreateFirstUserAsAdmin() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(repository.count()).thenReturn(0L);
        UserDTO dto = new UserDTO();
        dto.setName("name");
        dto.setPassword("password");

        service.create(dto);

        Mockito.verify(passwordHash).generate("password".toCharArray());
        Mockito.verify(repository).save(captor.capture());
        final User user = captor.getValue();
        assertEquals(dto.getName(), user.getName());
        assertEquals("hashPassword", user.getPassword());
        MatcherAssert.assertThat(user.getRoles(), Matchers.containsInAnyOrder(Role.ADMIN.name()));
    }

    @Test
    public void shouldCreateAsUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(repository.count()).thenReturn(1L);
        UserDTO dto = new UserDTO();
        dto.setName("name");
        dto.setPassword("password");

        service.create(dto);

        Mockito.verify(passwordHash).generate("password".toCharArray());
        Mockito.verify(repository).save(captor.capture());
        final User user = captor.getValue();
        assertEquals(dto.getName(), user.getName());
        assertEquals("hashPassword", user.getPassword());
        MatcherAssert.assertThat(user.getRoles(), Matchers.containsInAnyOrder(Role.USER.name()));
    }

    @Test
    public void shouldUpdatePasswordAsAdmin() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Principal principal = Mockito.mock(Principal.class);
        when(repository.findById("name"))
                .thenReturn(Optional.of(new User()));
        when(context.getCallerPrincipal()).thenReturn(principal);
        when(context.isCallerInRole(Role.ADMIN.name())).thenReturn(true);

        UserDTO dto = new UserDTO();
        dto.setName("name");
        dto.setPassword("password");

        service.updatePassword("name", dto);

        Mockito.verify(passwordHash).generate("password".toCharArray());
        Mockito.verify(repository).save(captor.capture());
        final User user = captor.getValue();
        assertEquals("hashPassword", user.getPassword());
    }

    @Test
    public void shouldUpdatePasswordAsUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Principal principal = Mockito.mock(Principal.class);
        when(repository.findById("name"))
                .thenReturn(Optional.of(new User()));
        when(context.getCallerPrincipal()).thenReturn(principal);
        when(context.isCallerInRole(Role.ADMIN.name())).thenReturn(false);
        when(principal.getName()).thenReturn("name");

        UserDTO dto = new UserDTO();
        dto.setName("name");
        dto.setPassword("password");

        service.updatePassword("name", dto);

        Mockito.verify(passwordHash).generate("password".toCharArray());
        Mockito.verify(repository).save(captor.capture());
        final User user = captor.getValue();
        assertEquals("hashPassword", user.getPassword());
    }

    @Test
    public void shouldNotUpdateWhenUserIsForbidden() {

        Principal principal = Mockito.mock(Principal.class);
        when(repository.findById("name"))
                .thenReturn(Optional.of(new User()));
        when(context.getCallerPrincipal()).thenReturn(principal);
        when(context.isCallerInRole(Role.ADMIN.name())).thenReturn(false);
        when(principal.getName()).thenReturn("other");

        UserDTO dto = new UserDTO();
        dto.setName("name");
        dto.setPassword("password");

        Assertions.assertThrows(UserForbiddenException.class, () -> service.updatePassword("name", dto));
    }
}