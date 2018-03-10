package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.network.NetworkManager;
import net.olympiccode.vhackos.api.network.Target;
import net.olympiccode.vhackos.api.requests.Response;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkManagerImpl implements NetworkManager {

    private vHackOSAPIImpl api;

    public NetworkManagerImpl(vHackOSAPIImpl api) {
        this.api = api;
    }


    public List<Target> getTargets() {
        Response r = Route.Network.TARGET_LIST.compile(api).getResponse();
        JSONObject object = r.getJSON();
        List<Target> targets = new ArrayList<>();
        try {
            JSONArray array = object.getJSONArray("ips");
            for (int i = 0; i < array.length(); i++) {
                JSONObject t = array.getJSONObject(i);
                String ip = t.getString("ip");
                int level = t.getInt("level");
                int fw = t.getInt("fw");
                boolean open = t.getInt("open") == 1;
                targets.add(new TargetImpl(api, ip, level, fw, open));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return targets;
    }

    public vHackOSAPIImpl getApi() {
        return api;
    }
}
