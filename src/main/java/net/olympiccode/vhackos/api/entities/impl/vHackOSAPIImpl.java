package net.olympiccode.vhackos.api.entities.impl;

import net.olympiccode.vhackos.api.appstore.AppManager;
import net.olympiccode.vhackos.api.entities.Stats;
import net.olympiccode.vhackos.api.events.Event;
import net.olympiccode.vhackos.api.events.EventListener;
import net.olympiccode.vhackos.api.events.StatsUpdateEvent;
import net.olympiccode.vhackos.api.misc.Leaderboards;
import net.olympiccode.vhackos.api.requests.Requester;
import net.olympiccode.vhackos.api.requests.Response;
import net.olympiccode.vhackos.api.requests.Route;
import net.olympiccode.vhackos.api.vHackOSAPI;
import okhttp3.OkHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class vHackOSAPIImpl implements vHackOSAPI {

    private static final Logger LOG = LoggerFactory.getLogger("vHackOSAPI");

    private final OkHttpClient.Builder httpClientBuilder;
    private List<EventListener> listeners = new ArrayList<>();
    private int corePoolSize;
    private Status status = Status.INITIALIZING;
    private Requester requester;
    private String username = null;
    private String password = null;
    private String accessToken = "";
    private String uid = "";
    private boolean invalidToken = false;
    private boolean debugResponses = false;
    private boolean preLogin;

    private StatsImpl stats = new StatsImpl(this);
    private AppManagerImpl appManager = new AppManagerImpl(this);
    private TaskManagerImpl taskManager = new TaskManagerImpl(this);
    private NetworkManagerImpl networkManager = new NetworkManagerImpl(this);
    private MinerImpl miner = new MinerImpl(this);
    private Leaderboards leaderboards = new LeaderboardsImpl(this);
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(corePoolSize, new APIThreadFactory());

    public vHackOSAPIImpl(OkHttpClient.Builder httpClientBuilder, boolean autoReconnect, int maxReconnectDelay, int corePoolSize, boolean preLogin) {
        this.httpClientBuilder = httpClientBuilder;
        boolean autoReconnect1 = autoReconnect;
        this.preLogin = preLogin;
        int maxReconnectDelay1 = maxReconnectDelay;
        this.requester = new Requester(this);
        this.corePoolSize = corePoolSize;
    }

    public void login(String username, String password, String... preLoginData) throws LoginException {
        setStatus(Status.LOGGING_IN);
        if (username == null || username.isEmpty())
            throw new LoginException("Provided username was null or empty!");
        if (password == null || password.isEmpty())
            throw new LoginException("Provided password was null or empty!");
        if (preLogin) {
            if (preLoginData[0] == null || preLoginData[0].isEmpty())
                throw new LoginException("Provided token was null or empty!");
            if (preLoginData[1] == null || preLoginData[1].isEmpty())
                throw new LoginException("Provided uid was null or empty!");
            this.accessToken = preLoginData[0];
            this.uid = preLoginData[1];
        }
        setUsername(username);
        setPassword(password);
        if (!preLogin) {
            setStatus(Status.AWAITING_LOGIN_CONFIRMATION);
            verifyDetails();
        } else {
            setStatus(Status.AWAITING_PRELOGIN_CHECK);
            verifyPreLogin();
        }
    }

    public void verifyPreLogin() throws LoginException {
        Route.CompiledRoute r = Route.Misc.UPDATE.compile(this);
        try {
            Response resp = getRequester().getResponse(r);
            if (!resp.getJSON().optString("username").isEmpty()) {
                setStatus(Status.CONNECTED);
                setup();
            } else {
                setStatus(Status.AWAITING_LOGIN_CONFIRMATION);
                verifyDetails();
            }
        } catch (Exception e) {
            setStatus(Status.AWAITING_LOGIN_CONFIRMATION);
            verifyDetails();
        }
    }

    public void verifyDetails() throws LoginException {
        Route.CompiledRoute r = Route.Misc.LOGIN.compile(this);
        try {
            Response resp = getRequester().getResponse(r);
            JSONObject userResponse = resp.getJSON();
            this.accessToken = userResponse.getString("accesstoken");
            this.uid = userResponse.getString("uid");
            setStatus(Status.CONNECTED);
            setup();
            LOG.info("Login Successful!");
        } catch (RuntimeException e) {
            Throwable ex = e.getCause() != null ? e.getCause().getCause() : null;
            if (ex instanceof LoginException)
                throw (LoginException) ex;
            else
                throw e;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setup() {
        setStatus(Status.LOADING_SUBSYSTEMS);
        LOG.info("Loading subsystems...");
        updateData();
        taskManager.reloadTasks();
        executorService.scheduleAtFixedRate(() -> updateData(), 30000, 30000, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(() -> taskManager.checkTasks(), 1000, 1000, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(() -> taskManager.reloadTasks(), 30000, 30000, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executorService.shutdownNow()));
        setStatus(Status.INITIALIZED);
    }

    private void updateData() {
        LOG.debug("Updating data");
        Route.CompiledRoute route = Route.Misc.UPDATE.compile(this);
        Response resp = getRequester().getResponse(route);
        try {
            JSONObject object = new JSONObject(resp.getString());
            stats.setExperience(Long.parseLong(object.optString("exp", "0")));
            stats.setExploits(Long.parseLong(object.optString("exploits", "0")));
            stats.setIpAddress(object.optString("ipaddress", "0"));
            stats.setLevel(Long.parseLong(object.optString("level", "0")));
            stats.setLevelPorcentage(Long.parseLong(object.optString("exppc", "0")));
            stats.setMoney(Long.parseLong(object.optString("money", "0")));
            stats.setUsername(object.optString("username"));
            stats.setNetcoins(Long.parseLong(object.optString("netcoins", "0")));
            stats.setRequiredExperience(Long.parseLong(object.optString("expreq", "0")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fireEvent(new StatsUpdateEvent(this));
    }

    public void fireEvent(Event e) {
        listeners.forEach(eventListener -> eventListener.onEvent(e));
    }

    public OkHttpClient.Builder getHttpClientBuilder() {
        return httpClientBuilder;
    }

    public void addEventListener(Object... listener) {
        for (Object o : listener) if (o instanceof EventListener) listeners.add((EventListener) o);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public Requester getRequester() {
        return requester;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public void removeEventListener(Object... listener) {
        for (Object o : listener) if (o instanceof EventListener) listeners.remove(o);
    }

    public boolean isDebugResponses() {
        return debugResponses;
    }

    public void setDebugResponses(boolean debugResponses) {
        this.debugResponses = debugResponses;
    }

    public List<EventListener> getRegisteredListeners() {
        return listeners;
    }

    public String getUid() {
        return uid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public AppManager getAppManager() {
        return appManager;
    }

    public Stats getStats() {
        return stats;
    }

    public TaskManagerImpl getTaskManager() {
        return taskManager;
    }

    public NetworkManagerImpl getNetworkManager() {
        return networkManager;
    }

    public MinerImpl getMiner() {
        return miner;
    }

    public Leaderboards getLeaderboards() {
        return leaderboards;
    }

    class APIThreadFactory implements ThreadFactory {
        private int counter = 0;
        private String prefix = "vHackOSAPI";

        public Thread newThread(Runnable r) {
            return new Thread(r, prefix + "-" + counter++);
        }
    }
}
