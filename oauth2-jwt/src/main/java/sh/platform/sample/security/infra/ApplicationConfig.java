package sh.platform.sample.security.infra;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("")
@ApplicationScoped
@DeclareRoles({"ADMIN", "MANAGER", "USER"})  // You need to indicate all roles that are used by the app
public class ApplicationConfig extends Application {




}
