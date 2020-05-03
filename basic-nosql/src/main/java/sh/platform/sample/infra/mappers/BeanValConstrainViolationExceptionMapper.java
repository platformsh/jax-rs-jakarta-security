package sh.platform.sample.infra.mappers;


import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class BeanValConstrainViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> cv = e.getConstraintViolations();
        final List<String> errors = cv.stream().map(c -> c.getPropertyPath() + " " + c.getMessage()).collect(Collectors.toList());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(errors))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }


}

