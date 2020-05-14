package sh.platform.sample.security;

import jakarta.nosql.mapping.Repository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
public interface UserRepository extends Repository<User, String> {

    Optional<User> findByNameAndPassword(String name, String password);

    Stream<User> findAll();
}
