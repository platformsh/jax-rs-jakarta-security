package sh.platform.sample;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
@RequestScoped
public class HelloWorldResource {

    @GET
    @PermitAll
    @Produces("text/plain")
    public String doGet() {
        return "hello from everyone";
    }

    @Path("admin")
    @GET
    @RolesAllowed("ADMIN")
    @Produces("text/plain")
    public String admin() {
        return "hello from admin";
    }

    @Path("manager")
    @GET
    @RolesAllowed({"MANAGER", "ADMIN"})
    @Produces("text/plain")
    public String manager() {
        return "hello from manager";
    }

    @Path("user")
    @GET
    @RolesAllowed({"MANAGER", "ADMIN", "USER"})
    @Produces("text/plain")
    public String user() {
        return "hello from user";
    }

    @Path("nobody")
    @GET
    @DenyAll
    @Produces("text/plain")
    public String nobody() {
        return "hello from nobody";
    }
}