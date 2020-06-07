package sh.platform.sample.security.oauth2;

import sh.platform.sample.security.SecurityService;
import sh.platform.sample.security.User;
import sh.platform.sample.security.UserNotAuthorizedException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.Duration;
import java.util.Set;

@ApplicationScoped
class Oauth2Service {

    static final int EXPIRE_IN = 3600;

    static final Duration EXPIRES = Duration.ofSeconds(EXPIRE_IN);

    @Inject
    private SecurityService securityService;

    @Inject
    private UserTokenRepository repository;

    @Inject
    private Validator validator;

    public Oauth2Response token(Oauth2Request request) {

        final Set<ConstraintViolation<Oauth2Request>> violations = validator.validate(request, Oauth2Request
                .GenerateToken.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        final User user = securityService.findBy(request.getUsername(), request.getPassword());
        final UserToken userToken = repository.findById(request.getUsername()).orElse(new UserToken(user.getName()));

        final Token token = Token.generate();

        final String jwt = UserJWT.createToken(user, token, EXPIRES);

        AccessToken accessToken = new AccessToken(jwt, token.get(), EXPIRES);
        RefreshToken refreshToken = new RefreshToken(Token.generate(), accessToken);
        userToken.add(refreshToken);
        repository.save(userToken);
        return Oauth2Response.of(accessToken, refreshToken, EXPIRE_IN);
    }

    public Oauth2Response refreshToken(Oauth2Request request) {
        final Set<ConstraintViolation<Oauth2Request>> violations = validator.validate(request, Oauth2Request
                .RefreshToken.class);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        final UserToken userToken = repository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new UserNotAuthorizedException("Invalid Token"));
        final User user = securityService.findBy(userToken.getUsername());
        final Token token = Token.generate();
        final String jwt = UserJWT.createToken(user, token, EXPIRES);
        AccessToken accessToken = new AccessToken(token.get(), jwt, EXPIRES);
        RefreshToken refreshToken = userToken.update(accessToken, request.getRefreshToken(), repository);

        return Oauth2Response.of(accessToken, refreshToken, EXPIRE_IN);
    }

}
