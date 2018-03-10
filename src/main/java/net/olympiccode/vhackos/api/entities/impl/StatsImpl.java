package net.olympiccode.vhackos.api.entities.impl;

import lombok.Getter;
import lombok.Setter;
import net.olympiccode.vhackos.api.entities.Stats;

@Setter
@Getter
public class StatsImpl implements Stats {
    private final vHackOSAPIImpl api;
    private long money, exploits, netcoins, level, experience, requiredExperience, levelPorcentage;
    private String ipAddress, username;

    public StatsImpl(vHackOSAPIImpl api) {
        this.api = api;
    }



}
