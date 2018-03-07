package net.olympiccode.vhackos.api.events;


import net.olympiccode.vhackos.api.vHackOSAPI;

public class Event {
    protected final vHackOSAPI api;

    public Event(vHackOSAPI api)
    {
        this.api = api;
    }

    public vHackOSAPI getAPI()
    {
        return api;
    }
}
