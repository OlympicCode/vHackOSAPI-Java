package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.misc.Leaderboards;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardsImpl implements Leaderboards {

    private final vHackOSAPIImpl api;
    private long tournamentEndTimestamp;
    private int tournamentRank;
    private int rank;
    private List<LeaderboardEntry> data;
    private List<TournamentEntry> tournamentData;
    private long lastReload = 0;
    public LeaderboardsImpl(vHackOSAPIImpl api) {
        this.api = api;
    }

    public long getTournamentEndTimestamp() {
        reload();
        return tournamentEndTimestamp;
    }

    public int getTournamentRank() {
        reload();
        return tournamentRank;
    }

    public int getRank() {
        reload();
        return rank;
    }

    public List<LeaderboardEntry> getData() {
        reload();
        return data;
    }

    public List<TournamentEntry> getTournamentData() {
        reload();
        return tournamentData;
    }
    
    public void reload() {
        if (lastReload <= System.currentTimeMillis() - 10000) {
            lastReload = System.currentTimeMillis();
            JSONObject object = Route.Misc.LEADERBOARDS.compile(api).getResponse().getJSON();
            tournamentRank = object.optInt("tournamentrank", 0);
            rank = object.optInt("myrank", 0);
            JSONArray dataa = object.optJSONArray("data");
            List<LeaderboardEntry> datal = new ArrayList<>();
            for (int i = 0; i < dataa.length(); i++) {
                JSONObject entry = dataa.optJSONObject(i);
                String user = entry.optString("user");
                int level = entry.optInt("level");
                long expPorcentage = Long.parseLong(entry.optString("exp").replace("%", ""));
                datal.add(new LeaderboardEntryImpl(user, level, expPorcentage));
            }
            data = datal;
            List<TournamentEntry> datal2 = new ArrayList<>();
            for (int i = 0; i < dataa.length(); i++) {
                JSONObject entry = dataa.optJSONObject(i);
                String user = entry.optString("user");
                int level = entry.optInt("level");
                long expGain = entry.optLong("expgain");
                datal2.add(new TournamentEntryImpl(user, level, expGain));
            }
            tournamentData = datal2;
            tournamentEndTimestamp = System.currentTimeMillis() + (object.optInt("tournamentleft", 0) * 1000);
        }
    }

    class LeaderboardEntryImpl implements Leaderboards.LeaderboardEntry {

        private String username;
        private int level;
        private long expPorcentage;

        public LeaderboardEntryImpl(String username, int level, long expPorcentage) {
           this.username = username;
           this.level = level;
           this.expPorcentage = expPorcentage;
        }
        public String getUsername() {
            return username;
        }

        public int getLevel() {
            return level;
        }


        public long getExpPorcentage() {
            return expPorcentage;
        }
    }

    class TournamentEntryImpl implements Leaderboards.TournamentEntry {

        private String username;
        private int level;
        private long expGain;

        public TournamentEntryImpl(String username, int level, long expGain) {
            this.username = username;
            this.level = level;
            this.expGain = expGain;
        }
        public String getUsername() {
            return username;
        }

        public int getLevel() {
            return level;
        }


        public long getExpGain() {
            return expGain;
        }
    }
}
