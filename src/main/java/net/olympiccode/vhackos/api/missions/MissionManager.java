package net.olympiccode.vhackos.api.missions;

import net.olympiccode.vhackos.api.entities.impl.MissionManagerImpl;

import java.util.List;

public interface MissionManager {
    boolean claimReward();
    int getRewardStage();
    boolean isRewardClaimed();
    List<DailyMission> getDailyMissions();
    enum RewardType {
        NetCoins, Boosters;
    }

    interface DailyMission {
        boolean claim();
        boolean isFinished();
        boolean isClaimed();
        int getExpReward();
        int getRewardAmount();
        RewardType getType();
    }


}
