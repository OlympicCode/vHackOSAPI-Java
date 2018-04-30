package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import lombok.Setter;
import net.olympiccode.vhackos.api.requests.Route;
import net.olympiccode.vhackos.api.server.Server;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class ServerImpl implements Server {

    public List<ServerNode> nodes = new ArrayList<>();
    int packages;
    int serverPieces;
    int firewallPieces;
    int antivirusPieces;
    long lastUpdate = 0;
    private vHackOSAPIImpl api;

    public ServerImpl(vHackOSAPIImpl api) {
        this.api = api;
        nodes.add(new ServerNodeImpl(NodeType.AV, 0, 0, 0, 0));
        nodes.add(new ServerNodeImpl(NodeType.AV, 1, 0, 0, 0));
        nodes.add(new ServerNodeImpl(NodeType.AV, 2, 0, 0, 0));

        nodes.add(new ServerNodeImpl(NodeType.FW, 0, 0, 0, 0));
        nodes.add(new ServerNodeImpl(NodeType.FW, 1, 0, 0, 0));
        nodes.add(new ServerNodeImpl(NodeType.FW, 2, 0, 0, 0));

        nodes.add(new ServerNodeImpl(NodeType.SERVER, 0, 0, 0, 0));
    }

    public boolean upgrade(NodeType type, int node) {
        JSONObject object = Route.Server.NODE_ADD.compile(api, node + "", type.getId() + "", "500").getResponse().getJSON();
        if (object.optInt("node_updated", 0) == 1) return true;
        update();
        return false;
    }

    public boolean upgradeFive(NodeType type, int node) {
        JSONObject object = Route.Server.NODE_ADD.compile(api, node + "", type.getId() + "", "600").getResponse().getJSON();
        if (object.optInt("node_updated", 0) == 2) return true;
        update();
        return false;
    }


    public OpenResult openAllPacks() {
        if (packages == 0) return null;
        JSONObject object = Route.Server.ACTION.compile(api, "2000").getResponse().getJSON();
        update();
        return new OpenResultImpl(object.optInt("sServer", 0), object.optInt("sAV", 0), object.optInt("sFW", 0), object.optInt("sBoost", 0));
    }

    public void update() {
        if (lastUpdate > System.currentTimeMillis() - 5000) return;
        lastUpdate = System.currentTimeMillis();
        JSONObject object = Route.Server.SERVER.compile(api).getResponse().getJSON();
        packages = object.optInt("packs", 0);

        serverPieces = object.optInt("server_pieces", 0);
        firewallPieces = object.optInt("fw_pieces", 0);
        antivirusPieces = object.optInt("av_pieces", 0);
 
        ((ServerNodeImpl) getNode(NodeType.AV , 0)).setStars(object.optInt("av1_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 0)).setStrength(object.optInt("av1_str", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 0)).setMaxStrength(object.optInt("av1_str_max", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 1)).setStars(object.optInt("av2_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 1)).setStrength(object.optInt("av2_str", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 1)).setMaxStrength(object.optInt("av2_str_max", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 2)).setStars(object.optInt("av3_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 2)).setStrength(object.optInt("av3_str", 0));
        ((ServerNodeImpl) getNode(NodeType.AV , 2)).setMaxStrength(object.optInt("av3_str_max", 0));

        ((ServerNodeImpl) getNode(NodeType.FW , 0)).setStars(object.optInt("fw1_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 0)).setStrength(object.optInt("fw1_str", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 0)).setMaxStrength(object.optInt("fw1_str_max", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 1)).setStars(object.optInt("fw2_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 1)).setStrength(object.optInt("fw2_str", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 1)).setMaxStrength(object.optInt("fw2_str_max", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 2)).setStars(object.optInt("fw3_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 2)).setStrength(object.optInt("fw3_str", 0));
        ((ServerNodeImpl) getNode(NodeType.FW , 2)).setMaxStrength(object.optInt("fw3_str_max", 0));

        ((ServerNodeImpl) getNode(NodeType.SERVER, 0)).setStars(object.optInt("server_str_stars", 0));
        ((ServerNodeImpl) getNode(NodeType.SERVER, 0)).setStrength(object.optInt("server_str", 0));
        ((ServerNodeImpl) getNode(NodeType.SERVER, 0)).setMaxStrength(object.optInt("server_str_max", 0));
    }

    public ServerNode getNode(NodeType type, int id) {
        update();
        return Optional.ofNullable(nodes.stream().filter(serverNode -> serverNode.getType().equals(type)).filter(serverNode -> serverNode.getId() == id).collect(Collectors.toList()).get(0)).orElse(null);
    }

    @Getter
    @Setter
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

    @Getter
    @Setter
    public class ServerNodeImpl implements ServerNode {
        NodeType type;
        int id, stars, strength, maxStrength;

        public ServerNodeImpl(NodeType type, int id, int stars, int strength, int maxStrength) {
            this.type = type;
            this.id = id;
            this.stars = stars;
            this.strength = strength;
            this.maxStrength = maxStrength;
            if (maxStrength > 0) nodes.add(this);
        }

        public boolean upgrade() {
            return ServerImpl.this.upgrade(type, id + 1);
        }
        public boolean upgradeFive() {
            return ServerImpl.this.upgradeFive(type, id + 1);
        }
    }


}
