package net.olympiccode.vhackos.api.appstore;

import net.olympiccode.vhackos.api.entities.impl.BruteForceImpl;

import java.util.List;

public interface TaskManager {
    List<Task> getActiveTasks();
    List<BruteForceImpl> getActiveBrutes();
    boolean finishAll();
    boolean boostAll();
    int getFinishAllCost();
    int getBoosters();
}
