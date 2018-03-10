package net.olympiccode.vhackos.api.events;


import net.olympiccode.vhackos.api.vHackOSAPI;

public class Event {
    final vHackOSAPI api;

    Event(vHackOSAPI api)
    {
        this.api = api;
    }

    public vHackOSAPI getAPI()
    {
        return api;
    }
}
