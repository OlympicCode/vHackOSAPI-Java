package net.olympiccode.vhackos.api.requests;

import net.olympiccode.vhackos.api.entities.impl.vHackOSAPIImpl;
import net.olympiccode.vhackos.api.utils.Encryption;
import net.olympiccode.vhackos.api.vHackOSAPI;
import net.olympiccode.vhackos.api.vHackOSInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Route {
    private final String route;
    private final String compilableRoute;
    private final int paramCount;
    private final String[] majorParameters;
    private final List<Integer> majorParamIndexes = new ArrayList<>();

    private Route(String route, String... majorParameters) {
        this.route = route;
        this.paramCount = majorParameters.length;
        this.majorParameters = majorParameters;
        compilableRoute = route + ".php?user=%s&pass=%s";
    }

    public String getRoute() {
        return route;
    }

    public String getCompilableRoute() {
        return compilableRoute;
    }

    public int getParamCount() {
        return paramCount;
    }

    public CompiledRoute compile(vHackOSAPI apir, String... params) {
        if (params.length != paramCount) {
            throw new IllegalArgumentException("Error Compiling Route: [" + route + "], incorrect amount of parameters provided." +
                    "Expected: " + (paramCount) + ", Provided: " + params.length);
        }


        vHackOSAPIImpl api = (vHackOSAPIImpl) apir;
        JSONObject arguments = new JSONObject();
        if (api.getStatus() != vHackOSAPI.Status.AWAITING_LOGIN_CONFIRMATION) {
            try {
                arguments.put("uid", api.getUid());
                arguments.put("accesstoken", api.getAccessToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                arguments.put("password", Encryption.md5Hash(api.getPassword()));
                arguments.put("username", api.getUsername());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AtomicInteger i = new AtomicInteger();
        for (String s : majorParameters) {
            try {
                arguments.put(s, params[i.getAndIncrement()]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String json = arguments.toString();


        String base64 = Encryption.base64Encrypt(json);
        String pass = Encryption.md5Hash(json + json + Encryption.md5Hash(json));
        String compiledRoute = vHackOSInfo.API_PREFIX + route + ".php?user=" + base64 + "&pass=" + pass;
        return new CompiledRoute(this, compiledRoute);
    }

    @Override
    public int hashCode() {
        return (route).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Route))
            return false;

        Route oRoute = (Route) o;
        return paramCount == (oRoute.paramCount) && route.equals(oRoute.route);
    }

    @Override
    public String toString() {
        return "Route(" + route + ")";
    }

    public static class Misc {
        public static final Route LOGIN = new Route("login");
        public static final Route UPDATE = new Route("update");
    }

    public static class AppStore {
        public static final Route APP_ACTION = new Route("store", "action", "appcode");
        public static final Route GET_APPS = new Route("store");
    }

    public static class Tasks {
        public static final Route GET_TASKS = new Route("tasks");
    }

    public class CompiledRoute {
        private final Route baseRoute;
        private final String compiledRoute;

        private CompiledRoute(Route baseRoute, String compiledRoute) {
            this.baseRoute = baseRoute;
            this.compiledRoute = compiledRoute;
        }

        public String getCompiledRoute() {
            return compiledRoute;
        }

        public Route getBaseRoute() {
            return baseRoute;
        }

        @Override
        public int hashCode() {
            return (compiledRoute).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CompiledRoute))
                return false;

            CompiledRoute oCompiled = (CompiledRoute) o;

            return baseRoute.equals(oCompiled.getBaseRoute()) && compiledRoute.equals(oCompiled.compiledRoute);
        }

        public Response getResponse(vHackOSAPIImpl api) {
            return api.getRequester().getResponse(this);
        }

        @Override
        public String toString() {
            return "CompiledRoute(" + compiledRoute + ")";
        }
    }
}