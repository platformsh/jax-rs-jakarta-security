package sh.platform.sample.security;

import jakarta.nosql.mapping.Repository;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public interface UserRepository extends Repository<User, String> {

    Stream<User> findAll();
}
