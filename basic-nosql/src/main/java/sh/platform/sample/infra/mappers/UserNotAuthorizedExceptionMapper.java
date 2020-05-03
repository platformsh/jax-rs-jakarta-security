package sh.platform.sample.infra.mappers;

import sh.platform.sample.security.UserForbiddenException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotAuthorizedExceptionMapper implements ExceptionMapper<UserForbiddenException> {

    @Override
    public Response toResponse(UserForbiddenException exception) {
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
