package net.olympiccode.vhackos.api;

import net.olympiccode.vhackos.api.appstore.AppManager;
import net.olympiccode.vhackos.api.appstore.TaskManager;
import net.olympiccode.vhackos.api.entities.Stats;
import net.olympiccode.vhackos.api.events.EventListener;
import net.olympiccode.vhackos.api.misc.Miner;
import net.olympiccode.vhackos.api.network.NetworkManager;

import java.util.List;

public interface vHackOSAPI {

    enum Status
    {
        INITIALIZING(true),
        INITIALIZED(true),
        LOGGING_IN(true),
        AWAITING_LOGIN_CONFIRMATION(true),
        LOADING_SUBSYSTEMS(true),
        CONNECTED(true),
        SHUTDOWN,
        FAILED_TO_LOGIN;

        private final boolean isInit;

        Status(boolean isInit)
        {
            this.isInit = isInit;
        }

        Status()
        {
            this.isInit = false;
        }

        public boolean isInit()
        {
            return isInit;
        }
    }

    Status getStatus();


    void addEventListener(Object... listeners);

    void removeEventListener(Object... listeners);

    List<EventListener> getRegisteredListeners();

    Stats getStats();

    AppManager getAppManager();

    TaskManager getTaskManager();

    NetworkManager getNetworkManager();

    Miner getMiner();

}

