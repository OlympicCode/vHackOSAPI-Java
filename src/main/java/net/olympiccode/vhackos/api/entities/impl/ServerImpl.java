package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import net.olympiccode.vhackos.api.requests.Route;
import net.olympiccode.vhackos.api.server.Server;
import org.json.JSONObject;

@Getter
public class ServerImpl implements Server {


    int packages;

    int serverPieces;
    int firewallPieces;
    int antivirusPieces;

    int serverStrength;
    int[] antivirusStrength = new int[3];
    int[] firewallStrength = new int[3];

    int serverStrengthMax;
    int[] antivirusStrengthMax = new int[3];
    int[] firewallStrengthMax = new int[3];

    int serverStars;
    int[] antivirusStars = new int[3];
    int[] firewallStars = new int[3];


    long lastUpdate = 0;
    private vHackOSAPIImpl api;

    public ServerImpl(vHackOSAPIImpl api) {
        this.api = api;
    }

    public boolean upgrade(NODE_TYPE type, int node) {
        update();
        JSONObject object = Route.Server.NODE_ADD.compile(api, node + "", type.getId() + "", "500").getResponse().getJSON();
        update();
        if (object.optInt("node_updated", 0) == 1) return true;
        return false;
    }

    public OpenResult openAllPacks() {
        update();
        if (packages == 0) return null;
        JSONObject object = Route.Server.ACTION.compile(api, "2000").getResponse().getJSON();
        update();
        return new OpenResultImpl(object.optInt("sServer", 0), object.optInt("sAV", 0), object.optInt("sFW", 0), object.optInt("sBoost", 0));
    }

    @Getter
    public class OpenResultImpl implements OpenResult {
        private final int fw;
        private final int server;
        private final int av;
        private final int boost;

        public OpenResultImpl(int server, int av, int fw, int boost) {
            this.server = server;
            this.av = av;
            this.fw = fw;
            this.boost = boost;
        }
    }

    public void update() {
        if (lastUpdate > System.currentTimeMillis() - 5000) return;
        lastUpdate = System.currentTimeMillis();
        JSONObject object = Route.Server.SERVER.compile(api).getResponse().getJSON();
        packages = object.optInt("packs", 0);

        serverPieces = object.optInt("server_pieces", 0);
        firewallPieces = object.optInt("fw_pieces", 0);
        antivirusPieces = object.optInt("av_pieces", 0);

        antivirusStrength[0] = object.optInt("av1_str", 0);
        antivirusStrength[1] = object.optInt("av2_str", 0);
        antivirusStrength[2] = object.optInt("av3_str", 0);

        firewallStrength[0] = object.optInt("fw1_str", 0);
        firewallStrength[1] = object.optInt("fw2_str", 0);
        firewallStrength[2] = object.optInt("fw3_str", 0);

        serverStrength = object.optInt("server_str", 0);
        serverStrengthMax = object.optInt("server_str_max", 0);
        serverStars = object.optInt("server_str_stars", 0);

        antivirusStrengthMax[0] = object.optInt("av1_str_max", 0);
        antivirusStrengthMax[1] = object.optInt("av2_str_max", 0);
        antivirusStrengthMax[2] = object.optInt("av3_str_max", 0);

        firewallStrengthMax[0] = object.optInt("fw1_str_max", 0);
        firewallStrengthMax[1] = object.optInt("fw2_str_max", 0);
        firewallStrengthMax[2] = object.optInt("fw3_str_max", 0);

        antivirusStars[0] = object.optInt("av1_str_stars", 0);
        antivirusStars[1] = object.optInt("av2_str_stars", 0);
        antivirusStars[2] = object.optInt("av3_str_stars", 0);

        firewallStars[0] = object.optInt("fw1_str_stars", 0);
        firewallStars[1] = object.optInt("fw2_str_stars", 0);
        firewallStars[2] = object.optInt("fw3_str_stars", 0);
    }

}
