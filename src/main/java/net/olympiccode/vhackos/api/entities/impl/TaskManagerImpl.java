package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import net.olympiccode.vhackos.api.appstore.Task;
import net.olympiccode.vhackos.api.appstore.TaskManager;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.entities.BruteForceState;
import net.olympiccode.vhackos.api.events.UpdateTaskFinishEvent;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManagerImpl implements TaskManager {

    private final vHackOSAPIImpl api;

    public TaskManagerImpl(vHackOSAPIImpl api) {
        this.api = api;
    }

    private List<TaskImpl> activeTasks = new ArrayList<>();
    @Getter
    public List<BruteForceImpl> activeBrutes = new ArrayList<>();
    @Getter
    private int finishAllCost = 0;
    @Getter
    private int boosters = 0;

    public void reloadTasks() {
        JSONObject obj = Route.Tasks.GET_TASKS.compile(api).getResponse().getJSON();
        try {
            this.finishAllCost = obj.optInt("finishallcosts", finishAllCost);
            this.boosters = obj.optInt("boosters", 0);
            if (obj.has("updates")) {
                JSONArray array = obj.getJSONArray("updates");
                checkTasks();
                List<TaskImpl> newlist = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject taskobj = array.getJSONObject(i);
                    newlist.add(new TaskImpl(AppType.byId(Integer.parseInt(taskobj.getString("appid"))), taskobj.getLong("start") * 1000, taskobj.getLong("end") * 1000, taskobj.getInt("level"), taskobj.getInt("id")));
                }
                activeTasks.clear();
                activeTasks.addAll(newlist);
            }
            if (obj.has("brutes")) {
                JSONArray array = obj.getJSONArray("brutes");
                activeBrutes.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject taskobj = array.getJSONObject(i);
                    int result =  taskobj.getInt("result");
                    BruteForceState state = result == 0 ? BruteForceState.RUNNING :  result == 1 ? BruteForceState.SUCCESS : BruteForceState.FAILED;
                    activeBrutes.add(new BruteForceImpl(api, state,taskobj.getString("user_ip"), taskobj.getLong("start") * 1000, taskobj.getLong("end") * 1000,taskobj.getString("username"), Integer.parseInt(taskobj.getString("id")))); }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Task getTaskById(int id) {
        return activeTasks.stream().filter(task -> task.getId() == id).collect(Collectors.toList()).get(0);
    }

    public List<Task> getActiveTasks() {
        return activeTasks.stream().map(task -> (Task) task).collect(Collectors.toList());
    }


    public boolean finishAll() {
        reloadTasks();
        if (activeTasks.size() < 1) return false;
       JSONObject object = Route.Tasks.FINISH.compile(api, "500", String.valueOf(activeTasks.get(0).getId())).getResponse().getJSON();
       if (object.optInt("finishall", 0) == 1) {
           ((StatsImpl) api.getStats()).setNetcoins(api.getStats().getNetcoins() - finishAllCost);
           for (TaskImpl activeTask : activeTasks) {
               activeTask.setEndTimestamp(0);
           }
           return true;
       }
       return false;
    }

    public boolean boostAll() {
        reloadTasks();
        JSONObject object = Route.Tasks.FINISH.compile(api, "888", String.valueOf(activeTasks.get(0).getId())).getResponse().getJSON();
        if (object.optInt("boosted", 0) == 1) {
            reloadTasks();
            return true;
        }
        return false;
    }

    public void checkTasks() {
        List<Task> toremove = new ArrayList<>();
        activeTasks.forEach(task -> {
            if (task.getEndTimestamp() <= System.currentTimeMillis()) {
                toremove.add(task);
                task.setFinished(true);

            }
        });
        toremove.forEach(task -> {
            activeTasks.remove(task);
            api.fireEvent(new UpdateTaskFinishEvent(api, (TaskImpl) task));
        });
    }

}
