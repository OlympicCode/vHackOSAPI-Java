package net.olympiccode.vhackos.api;

import net.olympiccode.vhackos.api.entities.impl.vHackOSAPIImpl;
import net.olympiccode.vhackos.api.utils.Checks;
import okhttp3.OkHttpClient;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class vHackOSAPIBuilder {
    private final List<Object> listeners;

    private OkHttpClient.Builder httpClientBuilder = null;
    private String username;
    private String password;
    private String token;
    private String uid;
    private boolean preLogin = false;
    private int[] sleepTime = {1000, 1500};

    public vHackOSAPIBuilder()
    {
        listeners = new LinkedList<>();
    }


    public vHackOSAPIBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public vHackOSAPIBuilder setSleepTime(int sleepTime) {
        this.sleepTime[0] = sleepTime;
        this.sleepTime[1] = -1;
        return this;
    }

    public vHackOSAPIBuilder setSleepTime(int min, int max) {
        this.sleepTime[0] = min;
        this.sleepTime[1] = max;
        return this;
    }

    public vHackOSAPIBuilder setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public vHackOSAPIBuilder setPreLogin(String token, String uid) {
        this.token = token;
        this.uid = uid;
        this.preLogin = true;
        return this;
    }

    public vHackOSAPIBuilder setHttpClientBuilder(OkHttpClient.Builder builder)
    {
        this.httpClientBuilder = builder;
        return this;
    }


    public vHackOSAPIBuilder addEventListener(Object... listeners)
    {
        Collections.addAll(this.listeners, listeners);
        return this;
    }


    public vHackOSAPIBuilder removeEventListener(Object... listeners)
    {
        this.listeners.removeAll(Arrays.asList(listeners));
        return this;
    }


    private vHackOSAPI buildAsync() throws LoginException, IllegalArgumentException
    {
        OkHttpClient.Builder httpClientBuilder = this.httpClientBuilder == null ? new OkHttpClient.Builder() : this.httpClientBuilder;
        boolean autoReconnect = true;
        int maxReconnectDelay = 900;
        vHackOSAPIImpl api = new vHackOSAPIImpl(httpClientBuilder, autoReconnect, maxReconnectDelay, 5, preLogin, sleepTime);

        listeners.forEach(api::addEventListener);
        api.setStatus(vHackOSAPI.Status.INITIALIZING);
        if (!preLogin) {
            api.login(username, password);
        } else {
            api.login(username, password, token, uid);
        }
        return api;
    }


    public vHackOSAPI buildBlocking() throws LoginException, IllegalArgumentException, InterruptedException
    {
        Checks.notNull(vHackOSAPI.Status.INITIALIZED, "Status");
        Checks.check(vHackOSAPI.Status.INITIALIZED.isInit(), "Cannot await the status %s as it is not part of the login cycle!", vHackOSAPI.Status.CONNECTED);
        vHackOSAPI api = buildAsync();
        while (!api.getStatus().isInit())
        {
            if (api.getStatus() == vHackOSAPI.Status.SHUTDOWN)
                throw new IllegalStateException("vHackOSAPI was unable to finish starting up!");
            Thread.sleep(50);
        }

        return api;
    }
}
