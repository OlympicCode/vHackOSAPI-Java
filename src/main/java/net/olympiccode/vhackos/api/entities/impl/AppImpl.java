package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import lombok.Setter;
import net.olympiccode.vhackos.api.appstore.App;
import net.olympiccode.vhackos.api.appstore.InstallableApp;
import net.olympiccode.vhackos.api.appstore.UpdateableApp;
import net.olympiccode.vhackos.api.entities.AppType;

@Getter
@Setter
public class AppImpl implements App {
    private AppType type;
    private long price;
    private int level;
    private int requiredLevel;
    private boolean oneTime;
    private int maxLevel;
    private vHackOSAPIImpl api;
    private boolean installed;

    public AppImpl(vHackOSAPIImpl api, AppType type, long price, int level, int requiredLevel, int maxLevel) {
        this.type = type;
        this.price = price;
        this.level = level;
        this.requiredLevel = requiredLevel;
        this.oneTime = maxLevel > 1;
        this.api = api;
        this.installed = level > 0;
        this.maxLevel = maxLevel;
    }

    public InstallableApp getAsInstallable() {
        return new InstallableAppImpl(api, type, price, level, requiredLevel, maxLevel);
    }


    public UpdateableApp getAsUpdateable() {
        return new UpdateableAppImpl(api, type, price, level, requiredLevel, maxLevel);
    }


}
