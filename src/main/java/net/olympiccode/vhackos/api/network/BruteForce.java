package net.olympiccode.vhackos.api.network;

import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.entities.BruteForceState;
import net.olympiccode.vhackos.api.vHackOSAPI;

public interface BruteForce {
    ExploitedTarget exploit();
    boolean remove();
    String getUsername();
    String getIp();
    vHackOSAPI getApi();
    long getStartTimestamp();
    long getEndTimestamp();
    long getId();
    BruteForceState getState();
    boolean retry();
}
