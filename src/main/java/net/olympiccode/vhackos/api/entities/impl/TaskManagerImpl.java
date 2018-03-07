package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import net.olympiccode.vhackos.api.appstore.Task;
import net.olympiccode.vhackos.api.appstore.TaskManager;
import net.olympiccode.vhackos.api.entities.AppType;
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

    public List<TaskImpl> activeTasks = new ArrayList<>();

    public void reloadTasks() {
        JSONObject obj = Route.Tasks.GET_TASKS.compile(api).getResponse(api).getJSON();
        try {
            if (!obj.has("updates")) return;
            JSONArray array = obj.getJSONArray("updates");
            checkTasks();
            activeTasks.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject taskobj = array.getJSONObject(i);
                activeTasks.add(new TaskImpl(AppType.byId(Integer.parseInt(taskobj.getString("appid"))), taskobj.getLong("start") * 1000, taskobj.getLong("end") * 1000, taskobj.getInt("level"), taskobj.getInt("id")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Task> getActiveTasks() {
        return activeTasks.stream().map(task -> (Task) task).collect(Collectors.toList());
    }

    public void checkTasks() {
        List<Task> toremove = new ArrayList<>();
        activeTasks.forEach(task -> {
            if (task.getEndTimestamp() <= System.currentTimeMillis()) {
                toremove.add(task);
                task.setFinished(true);
                api.fireEvent(new UpdateTaskFinishEvent(api, task));
            }
        });
        activeTasks.removeAll(toremove);
    }
}
