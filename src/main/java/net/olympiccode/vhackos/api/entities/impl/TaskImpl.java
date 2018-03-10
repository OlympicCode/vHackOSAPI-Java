package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import lombok.Setter;
import net.olympiccode.vhackos.api.appstore.Task;
import net.olympiccode.vhackos.api.entities.AppType;

@Getter
@Setter
public class TaskImpl implements Task {
    private long startTimestamp, endTimestamp, level, id;
    private AppType type;
    private boolean finished = false;

    public TaskImpl(AppType type, long startTimestamp, long endTimestamp, long level, long id) {
        this.startTimestamp = startTimestamp;
        this.type = type;
        this.endTimestamp = endTimestamp;
        this.level = level;
        this.id = id;
    }

}
