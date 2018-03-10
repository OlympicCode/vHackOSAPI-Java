package net.olympiccode.vhackos.api.network;

import net.olympiccode.vhackos.api.vHackOSAPI;

import java.util.List;

public interface NetworkManager {
    List<Target> getTargets();
    vHackOSAPI getApi();
}
