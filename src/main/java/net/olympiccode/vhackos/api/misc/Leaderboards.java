package net.olympiccode.vhackos.api.misc;

import java.util.List;

public interface Leaderboards {
    long getTournamentEndTimestamp();
    int getTournamentRank();
    int getRank();
    List<LeaderboardEntry> getData();
    List<TournamentEntry> getTournamentData();

    interface LeaderboardEntry {
       String getUsername();
       int getLevel();
       long getExpPorcentage();
    }

    interface TournamentEntry {
        String getUsername();
        int getLevel();
        long getExpGain();
    }
}
