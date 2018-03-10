package net.olympiccode.vhackos.api.events;

import net.olympiccode.vhackos.api.entities.Stats;
import net.olympiccode.vhackos.api.vHackOSAPI;

public class StatsUpdateEvent extends Event {
    public StatsUpdateEvent(vHackOSAPI api) {
       super(api);
    }

    public Stats getStats() {
        return api.getStats();
    }
}
