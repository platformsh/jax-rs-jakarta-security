package sh.platform.sample;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Collections;

import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

@ApplicationScoped
public class InMemoryIdentityStore implements IdentityStore {

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {

        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential user = UsernamePasswordCredential
                    .class.cast(credential);

            switch (user.getCaller()) {
                case "admin":
                    return new CredentialValidationResult("admin", Collections.singleton("ADMIN"));
                case "manager":
                    return new CredentialValidationResult("admin", Collections.singleton("MANAGER"));
                case "user":
                    return new CredentialValidationResult("admin", Collections.singleton("USER"));
                default:
                    return INVALID_RESULT;
            }

        }
        return INVALID_RESULT;
    }

}