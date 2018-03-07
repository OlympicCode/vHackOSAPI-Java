package net.olympiccode.vhackos.api.appstore;

import net.olympiccode.vhackos.api.entities.AppType;

public interface Task {
    boolean isFinished();
    long getLevel();
    AppType getType();
    long getId();
    long getEndTimestamp();
    long getStartTimestamp();
}
