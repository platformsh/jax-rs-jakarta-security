package sh.platform.sample.security.oauth2;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import sh.platform.sample.security.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class UserJWT {

    private static final Logger LOGGER = Logger.getLogger(UserJWT.class.getName());
    private static final String ISSUER = "jakarta";
    private static final String ROLES = "roles";

    private final String user;

    private final Set<String> roles;

    UserJWT(String user, Set<String> roles) {
        this.user = user;
        this.roles = roles;
    }

    public String getUser() {
        return user;
    }

    public Set<String> getRoles() {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles;
    }

    static String createToken(User user, Token token, Duration duration) {
        final LocalDateTime expire = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(duration.toMinutes());
        Algorithm algorithm = Algorithm.HMAC256(token.get());
        return JWT.create()
                .withJWTId(user.getName())
                .withIssuer(ISSUER)
                .withExpiresAt(Date.from(expire.atZone(ZoneOffset.UTC).toInstant()))
                .withClaim(ROLES, new ArrayList<>(user.getRoles()))
                .sign(algorithm);

    }

    static Optional<UserJWT> parse(String jwtText, String token) {
        Algorithm algorithm = Algorithm.HMAC256(token);
        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            final DecodedJWT jwt = verifier.verify(jwtText);
            final Claim roles = jwt.getClaim(ROLES);
            return Optional.of(new UserJWT(jwt.getId(),
                    roles.asList(String.class).stream().collect(Collectors.toUnmodifiableSet())));
        } catch (JWTVerificationException exp) {
            LOGGER.log(Level.WARNING, "There is an error to load the JWT token", exp);
            return Optional.empty();
        }
    }


}
