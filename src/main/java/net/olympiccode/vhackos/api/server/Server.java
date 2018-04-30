package net.olympiccode.vhackos.api.server;

import java.util.List;

public interface Server {

    int getServerPieces();

    int getFirewallPieces();
    int getAntivirusPieces();

    long getLastUpdate();

    OpenResult openAllPacks();

    boolean upgrade(NodeType type, int node);
    boolean upgradeFive(NodeType type, int node);

    int getPackages();

    interface OpenResult {
        int getFw();

        int getServer();

        int getAv();

        int getBoost();
    }

    ServerNode getNode(NodeType type, int id);

    List<ServerNode> getNodes();

    enum NodeType {
        SERVER(0), AV(1), FW(2);

        private final int id;

        NodeType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    interface ServerNode {
        NodeType getType();
        int getId();
        int getStars();
        int getStrength();
        int getMaxStrength();
        boolean upgrade();
        boolean upgradeFive();
    }
    
}
