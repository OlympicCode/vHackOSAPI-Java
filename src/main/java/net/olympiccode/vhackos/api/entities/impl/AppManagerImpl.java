package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.appstore.App;
import net.olympiccode.vhackos.api.appstore.AppManager;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.requests.Response;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AppManagerImpl implements AppManager {
    private final vHackOSAPIImpl api;

    public AppManagerImpl(vHackOSAPIImpl api) {
        this.api = api;
    }

    public List<App> getApps() {
        Route.CompiledRoute route =  Route.AppStore.GET_APPS.compile(api);
        Response r = api.getRequester().getResponse(route);
        JSONObject object = r.getJSON();
        List<App> l = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("apps");
            for (AppType type : AppType.values()) {
                JSONObject appobj = array.getJSONObject(type.getId());
                int price = appobj.optInt("price", -1);
                int level = appobj.optInt("level", 0);
                int requiredLevel = appobj.optInt("require", 1);
                int maxLevel = appobj.optInt("maxlvl", 1);
                l.add(new AppImpl(api, type, price, level, requiredLevel, maxLevel));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return l;
    }
    public App getApp(AppType type) {
        return getApps().get(type.getId());
    }

}
