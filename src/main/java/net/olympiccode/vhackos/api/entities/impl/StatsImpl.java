package net.olympiccode.vhackos.api.entities.impl;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile;
import lombok.Getter;
import lombok.Setter;
import net.olympiccode.vhackos.api.appstore.App;
import net.olympiccode.vhackos.api.entities.AppType;
import net.olympiccode.vhackos.api.entities.Stats;
import net.olympiccode.vhackos.api.requests.Response;
import net.olympiccode.vhackos.api.requests.Route;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
