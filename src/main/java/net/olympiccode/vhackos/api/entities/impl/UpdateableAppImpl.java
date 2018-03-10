package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.appstore.UpdateableApp;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.requests.Response;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONObject;

public class UpdateableAppImpl extends AppImpl implements UpdateableApp {

    private final vHackOSAPIImpl api;

    public UpdateableAppImpl(vHackOSAPIImpl api, AppType type, long price, int level, int requiredLevel, int maxLevel) {
        super(api, type, price, level, requiredLevel, maxLevel);
        this.api = api;
    }

    public boolean update() {
        Route.CompiledRoute route = Route.AppStore.APP_ACTION.compile(api, "100", String.valueOf(getType().getId()));
        Response r = api.getRequester().getResponse(route);
        JSONObject object = r.getJSON();
        if (Integer.valueOf(object.optString("updated", "1")) == 0) {
            api.getTaskManager().reloadTasks();
            return true;
        }
        return false;
    }

    public boolean fillTasks() {
        Route.CompiledRoute route = Route.AppStore.APP_ACTION.compile(api, "5500", String.valueOf(getType().getId()));
        Response r = api.getRequester().getResponse(route);
        JSONObject object = r.getJSON();
        if (Integer.valueOf(object.optString("filled", "1")) == 1) {
            api.getTaskManager().reloadTasks();
            return true;
        }
        return false;
    }
}
