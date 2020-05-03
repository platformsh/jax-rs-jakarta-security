package sh.platform.sample.security;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
class SecurityService {

    @Inject
    private UserRepository repository;

    @Inject
    private Pbkdf2PasswordHash passwordHash;

    @Inject
    private SecurityContext securityContext;

    void create(UserDTO userDTO) {
        User user = User.builder()
                .withPasswordHash(passwordHash)
                .withPassword(userDTO.getPassword())
                .withName(userDTO.getName())
                .withRoles(getRole())
                .build();
        repository.save(user);
    }

    void delete(String id) {
        repository.deleteById(id);
    }

    void updatePassword(String id, UserDTO dto) {

        final Principal principal = securityContext.getCallerPrincipal();
        if (isForbidden(id, securityContext, principal)) {
            throw new UserForbiddenException();
        }

        final User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.updatePassword(dto.getPassword(), passwordHash);
        repository.save(user);
    }


    public void addRole(String id, RoleDTO dto) {
        final User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.addRoles(dto.getRoles());
        repository.save(user);

    }

    public void removeRole(String id, RoleDTO dto) {
        final User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.removeRoles(dto.getRoles());
        repository.save(user);
    }

    public UserDTO getUser() {
        final Principal principal = securityContext.getCallerPrincipal();
        if (principal == null) {
            throw new UserNotAuthorizedException();
        }
        final User user = repository.findById(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));
        UserDTO dto = toDTO(user);
        return dto;
    }

    public List<UserDTO> getUsers() {
        return repository.findAll()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setName(user.getName());
        dto.setRoles(user.getRoles());
        return dto;
    }

    private Set<Role> getRole() {
        if (repository.count() == 0) {
            return Collections.singleton(Role.ADMIN);
        } else {
            return Collections.singleton(Role.USER);
        }
    }

    private boolean isForbidden(String id, SecurityContext context, Principal principal) {
        return !(context.isCallerInRole(Role.ADMIN.name()) || id.equals(principal.getName()));
    }


}
