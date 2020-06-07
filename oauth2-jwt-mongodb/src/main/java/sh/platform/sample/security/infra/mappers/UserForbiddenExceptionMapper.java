package sh.platform.sample.security.infra.mappers;

import sh.platform.sample.security.UserForbiddenException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserForbiddenExceptionMapper implements ExceptionMapper<UserForbiddenException> {
    @Override
    public Response toResponse(UserForbiddenException exception) {
        return Response.status(Response.Status.FORBIDDEN).build();
    }
}
