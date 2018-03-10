package net.olympiccode.vhackos.api.appstore;

import net.olympiccode.vhackos.api.entities.AppType;

public interface App {
    InstallableApp getAsInstallable();

    UpdateableApp getAsUpdateable();

    AppType getType();

    long getPrice();

    int getLevel();

    int getRequiredLevel();

    boolean isOneTime();

    boolean isInstalled();
}
