package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import net.olympiccode.vhackos.api.missions.MissionManager;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MissionManagerImpl implements MissionManager {

    private final vHackOSAPIImpl api;

    int rewardStage;
    boolean rewardClaimed;

    public MissionManagerImpl(vHackOSAPIImpl api) {
        this.api = api;
    }

    void update() {
        JSONObject json = Route.Missions.MISSIONS.compile(api).getResponse().getJSON();
        rewardStage = json.optInt("stage", 0);
        rewardClaimed = json.optInt("claim", 1) == 0;
    }

    public boolean claimReward() {
        if (rewardClaimed) return false;
        JSONObject json = Route.Missions.REWARD_CLAIM.compile(api, "100").getResponse().getJSON();
        if (json.optInt("claim") == 0) {
            rewardClaimed = true;
            return true;
        }
        return false;
    }

    public int getRewardStage() {
        return rewardStage;
    }

    public boolean isRewardClaimed() {
        return rewardClaimed;
    }

    public List<DailyMission> getDailyMissions() {
        List<DailyMission> list = new ArrayList<>();
        JSONObject json = Route.Missions.MISSIONS.compile(api).getResponse().getJSON();
        JSONArray array = json.optJSONArray("daily");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    list.add(new DailyMissionImpl(i, object.optInt("finished", 0) > 1, object.optInt("finished", 0) > 0,
                            object.optInt("exp", 0),
                            object.optInt("rewAmount", 0),
                            RewardType.valueOf(object.optString("rewType"))));
                }
            }
        }
        return list;
    }

    @Getter
    public class DailyMissionImpl implements DailyMission {
        boolean finished;
        boolean claimed;
        int expReward, rewardAmount;
        RewardType type;
        int id;
        public DailyMissionImpl(int id, boolean received, boolean finished, int expRewards, int rewardAmount, RewardType type) {
            this.claimed = received;
            this.expReward = expRewards;
            this.finished = finished;
            this.rewardAmount = rewardAmount;
            this.type = type;
            this.id = id;
        }

        public boolean claim() {
            if (claimed || !finished) return false;
            JSONObject object = Route.Missions.DAILY_RECEIVE.compile(api, "200", "" + id).getResponse().getJSON();
            if (object.optInt("claimed") == 1) return true;
            return false;
        }
    }
}
