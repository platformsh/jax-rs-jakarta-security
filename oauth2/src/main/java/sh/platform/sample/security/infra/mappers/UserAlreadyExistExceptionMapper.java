package sh.platform.sample.security.infra.mappers;

import sh.platform.sample.security.UserAlreadyExistException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserAlreadyExistExceptionMapper implements ExceptionMapper<UserAlreadyExistException> {

    @Override
    public Response toResponse(UserAlreadyExistException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorMessage(exception.getMessages()))
                .build();
    }
}
