package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import net.olympiccode.vhackos.api.exceptions.ExploitFailedException;
import net.olympiccode.vhackos.api.network.Target;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class containing a network target
 * <br>Used to exploit them
 */
@Getter
public class TargetImpl implements Target {

    private final int firewall;
    private final int level;
    private final String ip;
    private final boolean open;
    private final vHackOSAPIImpl api;

    public TargetImpl(vHackOSAPIImpl api, String ip, int level, int firewall, boolean open) {
        this.api = api;
        this.ip = ip;
        this.level = level;
        this.firewall = firewall;
        this.open = open;
    }

    /**
     * Exploits the target
     *
     * @return ExploitedTargetImpl The exploited target instance
     * @throws net.olympiccode.vhackos.api.exceptions.ExploitFailedException if the exploit fails with an error message
     */
    public ExploitedTargetImpl exploit() throws ExploitFailedException {
        if (api.getStats().getExploits() < 1) throw new ExploitFailedException("Not enough exploits");
       JSONObject object = Route.Network.EXPLOIT.compile(api, ip).getResponse().getJSON();
        ((StatsImpl) api.getStats()).setExploits( api.getStats().getExploits() -1);
        try {
            int result = object.getInt("result");
            switch (result){
                case 0:
                    return new ExploitedTargetImpl(api, ip);
                case 1:
                    throw new ExploitFailedException("Exploit not found");
                case 2:
                    throw new ExploitFailedException("Not enough exploits (This shouldn't happen?)");
                case 3:
                    throw new ExploitFailedException("Tutor exploit failed");
                case 4:
                    throw new ExploitFailedException("Not enough SDK");
                case 5:
                    throw new ExploitFailedException("Connection already opened (This shouldn't happen?)");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
