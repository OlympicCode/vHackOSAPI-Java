package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.appstore.InstallableApp;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.requests.Response;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONObject;

public class InstallableAppImpl extends AppImpl implements InstallableApp {

    private vHackOSAPIImpl api;
    private AppType type;
    public InstallableAppImpl(vHackOSAPIImpl api, AppType type, long price, int level, int requiredLevel, int maxLevel) {
        super(api, type, price, level, requiredLevel, maxLevel);
        this.api = api;
        this.type = type;
        if (level > 0) {
            throw new RuntimeException("Tried to get installable app of already installed app");
        }
    }

    public boolean hasRequiredLevel() {
        return api.getStats().getLevel() >= getRequiredLevel();
    }

    public boolean install() {
        Route.CompiledRoute route = Route.AppStore.APP_ACTION.compile(api, "200", String.valueOf(type.getId()));
        Response r = api.getRequester().getResponse(route);
        JSONObject object = r.getJSON();
        if (Integer.valueOf(object.optString("installed", "1")) == 0) {
            return true;
        }
        return false;
    }

}
