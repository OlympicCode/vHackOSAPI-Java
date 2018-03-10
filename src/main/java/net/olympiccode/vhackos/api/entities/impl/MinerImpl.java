package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.misc.Miner;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONObject;

public class MinerImpl implements Miner {

    private final vHackOSAPIImpl api;
    private Boolean installed;
    public MinerImpl(vHackOSAPIImpl api) {
        this.api = api;
    }

    private boolean checkInstall() {
        if (installed != null) return installed;
        installed = api.getAppManager().getApp(AppType.NCMiner).isInstalled();
        return installed;
    }
    public boolean isRunning() {
        if (!checkInstall()) return false;
        JSONObject obj = Route.Misc.MINER.compile(api).getResponse().getJSON();
        return obj.optInt("running", 0) == 1;
    }

    public boolean isDone() {
        if (!checkInstall()) return false;
        JSONObject obj = Route.Misc.MINER.compile(api).getResponse().getJSON();
        return obj.optInt("running", 0) == 2;
    }

    public boolean claim() {
        if (!checkInstall()) return false;
        if (isDone()) {
            JSONObject obj = Route.Misc.MINER_ACT.compile(api, "200").getResponse().getJSON();
            if(obj.optInt("claimed", 0) == 1) {
                ((StatsImpl)api.getStats()).setNetcoins(api.getStats().getNetcoins() + 100);
                return true;
            }
        }
        return false;
    }

    public boolean start() {
        if (!checkInstall()) return false;
        if (isDone()) {
            claim();
        }
        if (!isRunning() && !isDone()) {
            JSONObject obj = Route.Misc.MINER_ACT.compile(api, "100").getResponse().getJSON();
            if(obj.optInt("started", 0) == 1) {
                ((StatsImpl)api.getStats()).setNetcoins(api.getStats().getNetcoins() + 100);
                return true;
            }
        }
        return false;
    }
}
