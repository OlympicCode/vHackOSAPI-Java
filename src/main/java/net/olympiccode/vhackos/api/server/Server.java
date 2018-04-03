package net.olympiccode.vhackos.api.server;

public interface Server {

    int getServerPieces();

    int getFirewallPieces();
    int getAntivirusPieces();

    int getServerStrength();

    int[] getAntivirusStrength();

    int[] getFirewallStrength();

    int[] getAntivirusStrengthMax();

    int[] getFirewallStrengthMax();

    int[] getAntivirusStars();

    int[] getFirewallStars();

    int getServerStrengthMax();

    int getServerStars();

    long getLastUpdate();

    OpenResult openAllPacks();

    boolean upgrade(NODE_TYPE type, int node);

    int getPackages();

    void update();

    interface OpenResult {
        int getFw();

        int getServer();

        int getAv();

        int getBoost();
    }

    enum NODE_TYPE {
        SERVER(0), AV(1), FW(2);

        private final int id;

        NODE_TYPE(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
    
}
