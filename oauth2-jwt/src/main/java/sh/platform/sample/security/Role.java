package sh.platform.sample.security;

import java.util.function.Supplier;

public enum Role implements Supplier<String> {

    ADMIN, MANAGER, USER;

    @Override
    public String get() {
        return this.name();
    }
}
