package sh.platform.sample.security;

import java.util.Set;

public class RoleDTO {

    private Set<Role> roles;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roles=" + roles +
                '}';
    }
}
