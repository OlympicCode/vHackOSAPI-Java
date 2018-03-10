package net.olympiccode.vhackos.api.appstore;

public interface InstallableApp {
    boolean hasRequiredLevel();
    boolean install();
}
