package sh.platform.sample.security;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Entity
public class User {

    @Id
    private String name;

    @Column
    private String password;

    @Column
    private Set<Role> roles;

    User() {
    }

    String getName() {
        return name;
    }

    String getPassword() {
        return password;
    }

    Set<String> getRoles() {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream().map(Role::get)
                .collect(Collectors.toUnmodifiableSet());
    }

    void addRoles(Set<Role> roles) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.addAll(roles);
    }

    void removeRoles(Set<Role> roles) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.removeAll(roles);
    }

    void updatePassword(String password, Pbkdf2PasswordHash passwordHash) {
        this.password = passwordHash.generate(password.toCharArray());
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + name + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {

        private String name;

        private String password;

        private Set<Role> roles;

        private Pbkdf2PasswordHash passwordHash;

        private UserBuilder() {
        }

        public UserBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withRoles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public UserBuilder withPasswordHash(Pbkdf2PasswordHash passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public User build() {
            requireNonNull(name, "name is required");
            requireNonNull(password, "password is required");
            requireNonNull(roles, "roles is required");
            requireNonNull(passwordHash, "passwordHash is required");

            User user = new User();
            user.roles = roles;
            user.name = name;
            user.password = passwordHash.generate(password.toCharArray());
            return user;
        }
    }

}
