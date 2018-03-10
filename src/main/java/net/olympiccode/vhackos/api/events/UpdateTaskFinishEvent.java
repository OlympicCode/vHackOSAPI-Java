package net.olympiccode.vhackos.api.events;

import lombok.Getter;
import net.olympiccode.vhackos.api.appstore.Task;
import net.olympiccode.vhackos.api.entities.impl.TaskImpl;
import net.olympiccode.vhackos.api.vHackOSAPI;

public class UpdateTaskFinishEvent extends Event {
    @Getter
    Task task;
    public UpdateTaskFinishEvent(vHackOSAPI api, TaskImpl task) {
        super(api);
        this.task = task;
    }



}