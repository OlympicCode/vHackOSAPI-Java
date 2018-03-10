package net.olympiccode.vhackos.api.network;

import net.olympiccode.vhackos.api.exceptions.ExploitFailedException;
import net.olympiccode.vhackos.api.vHackOSAPI;

public interface Target {
    ExploitedTarget exploit() throws ExploitFailedException;
    int getFirewall();
    int getLevel();
    String getIp();
    boolean isOpen();
    vHackOSAPI getApi();
}
