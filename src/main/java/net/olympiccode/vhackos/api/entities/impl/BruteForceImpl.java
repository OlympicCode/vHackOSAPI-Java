package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.entities.BruteForceState;
import net.olympiccode.vhackos.api.exceptions.ExploitFailedException;
import net.olympiccode.vhackos.api.network.BruteForce;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONObject;

@Getter
public class BruteForceImpl implements BruteForce {
    private final String username;
    private final String ip;
    private final vHackOSAPIImpl api;
    private long startTimestamp, endTimestamp, id;
    private BruteForceState state;

    public BruteForceImpl(vHackOSAPIImpl api, BruteForceState state, String ip, long startTimestamp, long endTimestamp, String username, long id) {
        this.state = state;
        this.ip = ip;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.username = username;
        this.id = id;
        this.api = api;
    }

    public ExploitedTargetImpl exploit() {
        try {
            return new ExploitedTargetImpl(api, ip);
        } catch (ExploitFailedException e) {
            return null;
        }
    }

    public boolean remove() {
        JSONObject obj = Route.Tasks.REMOVE_BRUTE.compile(api, "10000", String.valueOf(id)).getResponse().getJSON();
        if (obj.optInt("bruteremoved", 0) == 1) {
            api.getTaskManager().activeBrutes.remove(this);
            return true;
        }
        return false;
    }
}
